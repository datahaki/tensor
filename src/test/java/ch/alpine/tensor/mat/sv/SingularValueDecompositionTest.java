// code by jph
package ch.alpine.tensor.mat.sv;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SingularValueDecompositionTest extends TestCase {
  public void testSvd1() {
    Random random = new Random(3);
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), random, 8, 5);
    SingularValueDecomposition svd = TestHelper.svd(mat);
    assertEquals(MatrixRank.of(svd), 5);
  }

  public void testSvd1Units() {
    Random random = new Random(3);
    Tensor mat = RandomVariate.of(NormalDistribution.of(Quantity.of(1, "s"), Quantity.of(2, "s")), random, 8, 5);
    SingularValueDecomposition svd = TestHelper.svd(mat);
    assertEquals(MatrixRank.of(svd), 5);
  }

  public void testSvd2() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3 }, { 0, 0, 4 }, { 0, 0, 0 } });
    TestHelper.svd(mat.dot(B).map(s -> Quantity.of(s, "A")));
    SingularValueDecomposition svd = TestHelper.svd(mat.dot(B));
    assertEquals(MatrixRank.of(svd), 2);
    TestHelper.svd(svd.getU());
    TestHelper.svd(svd.getV());
    TestHelper.svd(Transpose.of(PseudoInverse.of(svd)));
    TestHelper.svd(Transpose.of(NullSpace.of(svd)));
  }

  public void testSvd3() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 20, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    TestHelper.svd(A.map(s -> Quantity.of(s, "A")));
    SingularValueDecomposition svd = TestHelper.svd(A);
    assertEquals(MatrixRank.of(svd), 3);
    TestHelper.svd(svd.getU());
    TestHelper.svd(svd.getV());
    TestHelper.svd(Transpose.of(PseudoInverse.of(svd)));
    TestHelper.svd(Transpose.of(NullSpace.of(svd)));
  }

  public void testSvdNull() {
    int n = 20;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), n, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    TestHelper.svd(A.map(s -> Quantity.of(s, "V")));
    SingularValueDecomposition svd = TestHelper.svd(A);
    assertEquals(MatrixRank.of(svd), 3);
    Tensor nls = NullSpace.of(svd);
    Tensor nul = A.dot(nls.get(0));
    assertEquals(Tolerance.CHOP.of(nul), Array.zeros(n));
  }

  public void testSvd4() {
    Random random = new Random(1);
    int n = 11;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), random, n, n);
    SingularValueDecomposition svd = TestHelper.svd(mat);
    Tolerance.CHOP.requireClose(PseudoInverse.of(svd), Inverse.of(mat)); // 1e-12 does not always work
    assertEquals(MatrixRank.of(svd), n);
    Tolerance.CHOP.requireClose(PseudoInverse.of(svd).dot(mat), IdentityMatrix.of(n));
  }

  public void testSvdR1() {
    Random random = new Random(1);
    int n = 15;
    Tensor matrix = Tensors.matrix((r, c) -> RationalScalar.of(random.nextInt(1000) - 500, random.nextInt(1000) + 1), n, n);
    SingularValueDecomposition svd = TestHelper.svd(matrix);
    if (MatrixRank.of(svd) == n) // 1e-12 failed in the past
      Chop._08.requireClose(PseudoInverse.of(svd), Inverse.of(matrix));
  }

  public void testSvdR2() {
    Random random = new Random(1);
    Tensor mat = Tensors.matrix((r, c) -> RationalScalar.of(random.nextInt(100) - 50, random.nextInt(100) + 1), 20, 4);
    Tensor B = Tensors.matrix(new Scalar[][] { //
        // "{1, 2, 3, -1}"
        { RationalScalar.of(1, 1), RationalScalar.of(2, 1), RationalScalar.of(3, 1), RationalScalar.of(-1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(4, 1), RationalScalar.of(2, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1) } });
    SingularValueDecomposition svd = TestHelper.svd(mat.dot(B));
    assertEquals(MatrixRank.of(svd), 3);
  }

  public void testSo3() {
    Tensor ad = LeviCivitaTensor.of(3);
    Tensor sk = ad.dot(Tensors.vector(1, 1, 1));
    SingularValueDecomposition svd = TestHelper.svd(sk);
    assertEquals(MatrixRank.of(svd), 2);
  }

  public void testFullConstant() {
    Tensor d = Tensors.matrix((i, j) -> RealScalar.of(1e-10), 10, 10);
    SingularValueDecomposition svd = TestHelper.svd(d);
    assertEquals(MatrixRank.of(svd), 1);
  }

  public void testHilbert1() {
    Tensor d = HilbertMatrix.of(200, 20);
    SingularValueDecomposition svd = TestHelper.svd(d);
    assertTrue(13 <= MatrixRank.of(svd));
  }

  public void testHilbert2() {
    Tensor d = HilbertMatrix.of(100, 10);
    SingularValueDecomposition svd = TestHelper.svd(d);
    assertEquals(10, MatrixRank.of(svd));
  }

  public void testJordan1() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1e-10, 1, 1, 1, 1e-10));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j, j + 1));
    SingularValueDecomposition svd = TestHelper.svd(d);
    assertEquals(MatrixRank.of(svd), 5);
  }

  public void testJordan2() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 1));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j + 1, j));
    TestHelper.svd(d);
  }

  public void testScdR3() {
    int n = 10;
    int k = 8;
    Tensor mat = Array.zeros(n, n);
    mat.set(RationalScalar.of(1, 1), k - 4, k - 1);
    mat.set(RationalScalar.of(1, 1), k - 1, k - 4);
    SingularValueDecomposition svd = TestHelper.svd(mat);
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

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(SingularValueDecomposition.class.getModifiers()));
  }
}
