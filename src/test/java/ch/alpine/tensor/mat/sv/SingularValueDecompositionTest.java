// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
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
import ch.alpine.tensor.mat.re.MatrixRankSvd;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

public class SingularValueDecompositionTest {
  @Test
  public void testSvd1() {
    Random random = new Random(3);
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), random, 8, 5);
    SingularValueDecomposition svd = InitTest.svd(mat);
    assertEquals(MatrixRankSvd.of(svd), 5);
  }

  @Test
  public void testSvd1Units() {
    Random random = new Random(3);
    Tensor mat = RandomVariate.of(NormalDistribution.of(Quantity.of(1, "s"), Quantity.of(2, "s")), random, 8, 5);
    SingularValueDecomposition svd = InitTest.svd(mat);
    assertEquals(MatrixRankSvd.of(svd), 5);
  }

  @Test
  public void testSvd2() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3 }, { 0, 0, 4 }, { 0, 0, 0 } });
    InitTest.svd(mat.dot(B).map(s -> Quantity.of(s, "A")));
    SingularValueDecomposition svd = InitTest.svd(mat.dot(B));
    assertEquals(MatrixRankSvd.of(svd), 2);
    InitTest.svd(svd.getU());
    InitTest.svd(svd.getV());
    InitTest.svd(Transpose.of(PseudoInverse.of(svd)));
    InitTest.svd(Transpose.of(NullSpace.of(svd)));
  }

  @Test
  public void testSvd3() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 20, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    InitTest.svd(A.map(s -> Quantity.of(s, "A")));
    SingularValueDecomposition svd = InitTest.svd(A);
    assertEquals(MatrixRankSvd.of(svd), 3);
    InitTest.svd(svd.getU());
    InitTest.svd(svd.getV());
    InitTest.svd(Transpose.of(PseudoInverse.of(svd)));
    InitTest.svd(Transpose.of(NullSpace.of(svd)));
  }

  @Test
  public void testSvdNull() {
    int n = 20;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), n, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    InitTest.svd(A.map(s -> Quantity.of(s, "V")));
    SingularValueDecomposition svd = InitTest.svd(A);
    assertEquals(MatrixRankSvd.of(svd), 3);
    Tensor nls = NullSpace.of(svd);
    Tensor nul = A.dot(nls.get(0));
    assertEquals(Tolerance.CHOP.of(nul), Array.zeros(n));
  }

  @Test
  public void testSvd4() {
    Random random = new Random(1);
    int n = 11;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), random, n, n);
    SingularValueDecomposition svd = InitTest.svd(mat);
    Tolerance.CHOP.requireClose(PseudoInverse.of(svd), Inverse.of(mat)); // 1e-12 does not always work
    assertEquals(MatrixRankSvd.of(svd), n);
    Tolerance.CHOP.requireClose(PseudoInverse.of(svd).dot(mat), IdentityMatrix.of(n));
  }

  @Test
  public void testSvdR1() {
    Random random = new Random(1);
    int n = 15;
    Tensor matrix = Tensors.matrix((r, c) -> RationalScalar.of(random.nextInt(1000) - 500, random.nextInt(1000) + 1), n, n);
    SingularValueDecomposition svd = InitTest.svd(matrix);
    if (MatrixRankSvd.of(svd) == n) // 1e-12 failed in the past
      Chop._08.requireClose(PseudoInverse.of(svd), Inverse.of(matrix));
  }

  @Test
  public void testSvdR2() {
    Random random = new Random(1);
    Tensor mat = Tensors.matrix((r, c) -> RationalScalar.of(random.nextInt(100) - 50, random.nextInt(100) + 1), 20, 4);
    Tensor B = Tensors.matrix(new Scalar[][] { //
        // "{1, 2, 3, -1}"
        { RationalScalar.of(1, 1), RationalScalar.of(2, 1), RationalScalar.of(3, 1), RationalScalar.of(-1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(4, 1), RationalScalar.of(2, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1) } });
    SingularValueDecomposition svd = InitTest.svd(mat.dot(B));
    assertEquals(MatrixRankSvd.of(svd), 3);
  }

  @Test
  public void testSo3() {
    Tensor ad = LeviCivitaTensor.of(3);
    Tensor sk = ad.dot(Tensors.vector(1, 1, 1));
    SingularValueDecomposition svd = InitTest.svd(sk);
    assertEquals(MatrixRankSvd.of(svd), 2);
  }

  @Test
  public void testFullConstant() {
    Tensor d = Tensors.matrix((i, j) -> RealScalar.of(1e-10), 10, 10);
    SingularValueDecomposition svd = InitTest.svd(d);
    assertEquals(MatrixRankSvd.of(svd), 1);
  }

  @Test
  public void testHilbert1() {
    Tensor d = HilbertMatrix.of(200, 20);
    SingularValueDecomposition svd = InitTest.svd(d);
    assertTrue(13 <= MatrixRankSvd.of(svd));
  }

  @Test
  public void testHilbert2() {
    Tensor d = HilbertMatrix.of(100, 10);
    SingularValueDecomposition svd = InitTest.svd(d);
    assertEquals(10, MatrixRankSvd.of(svd));
  }

  @Test
  public void testJordan1() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1e-10, 1, 1, 1, 1e-10));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j, j + 1));
    SingularValueDecomposition svd = InitTest.svd(d);
    assertEquals(MatrixRankSvd.of(svd), 5);
  }

  @Test
  public void testJordan2() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 1));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j + 1, j));
    InitTest.svd(d);
  }

  @Test
  public void testScdR3() {
    int n = 10;
    int k = 8;
    Tensor mat = Array.zeros(n, n);
    mat.set(RationalScalar.of(1, 1), k - 4, k - 1);
    mat.set(RationalScalar.of(1, 1), k - 1, k - 4);
    SingularValueDecomposition svd = InitTest.svd(mat);
    assertEquals(MatrixRankSvd.of(svd), 2);
    assertEquals(Sort.of(svd.values()), Tensors.fromString("{0, 0, 0, 0, 0, 0, 0, 0, 1.0, 1.0}"));
  }

  @Test
  public void testEye() {
    assertEquals(MatrixRankSvd.of(IdentityMatrix.of(10)), 10);
    assertEquals(MatrixRankSvd.of(DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 0, 0))), 4);
  }

  @Test
  public void testFail() {
    assertThrows(TensorRuntimeException.class, () -> SingularValueDecomposition.of(RealScalar.ONE));
    assertThrows(TensorRuntimeException.class, () -> SingularValueDecomposition.of(Tensors.vector(1, 2, 3)));
    assertThrows(ClassCastException.class, () -> SingularValueDecomposition.of(Tensors.fromString("{{1, 2}, {2, {3}}}")));
    assertThrows(IllegalArgumentException.class, () -> SingularValueDecomposition.of(Array.zeros(2, 3, 4)));
  }

  @Test
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(SingularValueDecomposition.class.getModifiers()));
  }
}
