// code by jph
package ch.alpine.tensor.mat.sv;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SingularValueDecompositionTest extends TestCase {
  static SingularValueDecomposition specialOps(Tensor A) {
    SingularValueDecomposition svd = SingularValueDecomposition.of(A);
    List<Integer> dims = Dimensions.of(A);
    int N = dims.get(1);
    final Tensor U = svd.getU();
    assertEquals(dims, Dimensions.of(U));
    final Tensor w = svd.values();
    final Tensor V = svd.getV();
    Tensor W = DiagonalMatrix.with(w);
    Tensor UtU = Tolerance.CHOP.of(Transpose.of(U).dot(U).subtract(IdentityMatrix.of(N)));
    assertEquals(UtU, Array.zeros(N, N));
    Tensor VVt = Tolerance.CHOP.of(MatrixDotTranspose.of(V, V).subtract(IdentityMatrix.of(N)));
    assertEquals(VVt, Array.zeros(N, N));
    Tensor VtV = Tolerance.CHOP.of(Transpose.of(V).dot(V).subtract(IdentityMatrix.of(N)));
    assertEquals(VtV, Array.zeros(N, N));
    Tensor UWVt = Tolerance.CHOP.of(MatrixDotTranspose.of(U.dot(W), V).subtract(A));
    assertEquals(UWVt, Array.zeros(Dimensions.of(UWVt)));
    Tensor UW_AV = Tolerance.CHOP.of(U.dot(W).subtract(A.dot(V)));
    assertEquals(UW_AV, Array.zeros(Dimensions.of(UW_AV)));
    assertTrue(w.stream().map(Scalar.class::cast).noneMatch(Sign::isNegative));
    if (MatrixRank.of(svd) < N) {
      Tensor nul = NullSpace.of(svd);
      Tensor res = MatrixDotTranspose.of(A, nul);
      assertEquals(Tolerance.CHOP.of(res), Array.zeros(Dimensions.of(res)));
    }
    return svd;
  }

  public void testSvd1() {
    Random random = new Random(3);
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), random, 8, 5);
    SingularValueDecomposition svd = specialOps(mat);
    assertEquals(MatrixRank.of(svd), 5);
  }

  public void testSvd2() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3 }, { 0, 0, 4 }, { 0, 0, 0 } });
    SingularValueDecomposition svd = specialOps(mat.dot(B));
    assertEquals(MatrixRank.of(svd), 2);
    specialOps(svd.getU());
    specialOps(svd.getV());
    specialOps(Transpose.of(PseudoInverse.of(svd)));
    specialOps(Transpose.of(NullSpace.of(svd)));
  }

  public void testSvd3() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 20, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    SingularValueDecomposition svd = specialOps(A);
    assertEquals(MatrixRank.of(svd), 3);
    specialOps(svd.getU());
    specialOps(svd.getV());
    specialOps(Transpose.of(PseudoInverse.of(svd)));
    specialOps(Transpose.of(NullSpace.of(svd)));
  }

  public void testSvdNull() {
    int n = 20;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), n, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    SingularValueDecomposition svd = specialOps(A);
    assertEquals(MatrixRank.of(svd), 3);
    Tensor nls = NullSpace.of(svd);
    Tensor nul = A.dot(nls.get(0));
    assertEquals(Tolerance.CHOP.of(nul), Array.zeros(n));
  }

  public void testSvd4() {
    int n = 11;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), n, n);
    SingularValueDecomposition svd = specialOps(mat);
    Chop._10.requireClose(PseudoInverse.of(svd), Inverse.of(mat)); // 1e-12 does not always work
    assertEquals(MatrixRank.of(svd), n);
    Chop._10.requireClose(PseudoInverse.of(svd).dot(mat), IdentityMatrix.of(n));
  }

  private static final Random RANDOM = new Random();

  public void testSvdR1() {
    int n = 15;
    Tensor matrix = Tensors.matrix((r, c) -> RationalScalar.of(RANDOM.nextInt(1000) - 500, RANDOM.nextInt(1000) + 1), n, n);
    SingularValueDecomposition svd = specialOps(matrix);
    if (MatrixRank.of(svd) == n) // 1e-12 failed in the past
      Chop._08.requireClose(PseudoInverse.of(svd), Inverse.of(matrix));
  }

  public void testSvdR2() {
    Tensor mat = Tensors.matrix((r, c) -> RationalScalar.of(RANDOM.nextInt(100) - 50, RANDOM.nextInt(100) + 1), 20, 4);
    Tensor B = Tensors.matrix(new Scalar[][] { //
        // "{1, 2, 3, -1}"
        { RationalScalar.of(1, 1), RationalScalar.of(2, 1), RationalScalar.of(3, 1), RationalScalar.of(-1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(4, 1), RationalScalar.of(2, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1) } });
    SingularValueDecomposition svd = specialOps(mat.dot(B));
    assertEquals(MatrixRank.of(svd), 3);
  }

  public void testSo3() {
    Tensor ad = LeviCivitaTensor.of(3);
    Tensor sk = ad.dot(Tensors.vector(1, 1, 1));
    SingularValueDecomposition svd = specialOps(sk);
    assertEquals(MatrixRank.of(svd), 2);
  }

  public void testFullConstant() {
    Tensor d = Tensors.matrix((i, j) -> RealScalar.of(1e-10), 10, 10);
    SingularValueDecomposition svd = specialOps(d);
    assertEquals(MatrixRank.of(svd), 1);
  }

  public void testHilbert1() {
    Tensor d = HilbertMatrix.of(200, 20);
    SingularValueDecomposition svd = specialOps(d);
    assertTrue(13 <= MatrixRank.of(svd));
  }

  public void testHilbert2() {
    Tensor d = HilbertMatrix.of(100, 10);
    SingularValueDecomposition svd = specialOps(d);
    assertEquals(10, MatrixRank.of(svd));
  }

  public void testJordan1() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1e-10, 1, 1, 1, 1e-10));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j, j + 1));
    SingularValueDecomposition svd = specialOps(d);
    assertEquals(MatrixRank.of(svd), 5);
  }

  public void testJordan2() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 1));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j + 1, j));
    specialOps(d);
  }

  public void testScdR3() {
    int n = 10;
    int k = 8;
    Tensor mat = Array.zeros(n, n);
    mat.set(RationalScalar.of(1, 1), k - 4, k - 1);
    mat.set(RationalScalar.of(1, 1), k - 1, k - 4);
    SingularValueDecomposition svd = specialOps(mat);
    assertEquals(MatrixRank.of(svd), 2);
    assertEquals(Sort.of(svd.values()), Tensors.fromString("{0, 0, 0, 0, 0, 0, 0, 0, 1.0, 1.0}"));
  }

  public void testEye() {
    assertEquals(MatrixRank.usingSvd(IdentityMatrix.of(10)), 10);
    assertEquals(MatrixRank.usingSvd(DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 0, 0))), 4);
  }

  public void testFail() {
    AssertFail.of(() -> SingularValueDecomposition.of(RealScalar.ONE));
    AssertFail.of(() -> SingularValueDecomposition.of(Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> SingularValueDecomposition.of(Tensors.fromString("{{1, 2}, {2, {3}}}")));
    AssertFail.of(() -> SingularValueDecomposition.of(Array.zeros(2, 3, 4)));
  }
}
