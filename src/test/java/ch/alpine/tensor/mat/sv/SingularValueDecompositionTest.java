// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.VectorQ;
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

class SingularValueDecompositionTest {
  @Test
  void testSvd1() {
    Random random = new Random(3);
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), random, 8, 5);
    InitTest.svd(mat);
    assertEquals(MatrixRank.of(mat), 5);
  }

  @Test
  void testSvd1Units() {
    Random random = new Random(3);
    Tensor mat = RandomVariate.of(NormalDistribution.of(Quantity.of(1, "s"), Quantity.of(2, "s")), random, 8, 5);
    InitTest.svd(mat);
    assertEquals(MatrixRank.of(mat), 5);
  }

  @Test
  void testSvd2() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3 }, { 0, 0, 4 }, { 0, 0, 0 } });
    InitTest.svd(mat.dot(B).map(s -> Quantity.of(s, "A")));
    SingularValueDecomposition svd = InitTest.svd(mat.dot(B));
    assertEquals(MatrixRank.of(mat.dot(B)), 2);
    InitTest.svd(svd.getU());
    InitTest.svd(svd.getV());
    InitTest.svd(Transpose.of(PseudoInverse.of(svd)));
    InitTest.svd(Transpose.of(NullSpace.of(svd)));
  }

  @Test
  void testSvd3() {
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), 20, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    InitTest.svd(A.map(s -> Quantity.of(s, "A")));
    SingularValueDecomposition svd = InitTest.svd(A);
    assertEquals(MatrixRank.of(A), 3);
    InitTest.svd(svd.getU());
    InitTest.svd(svd.getV());
    InitTest.svd(Transpose.of(PseudoInverse.of(svd)));
    InitTest.svd(Transpose.of(NullSpace.of(svd)));
  }

  @Test
  void testSvdNull() {
    int n = 20;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), n, 4);
    Tensor B = Tensors.matrixLong(new long[][] { //
        { 1, 2, 3, -1 }, { 0, 0, 4, 2 }, { 0, 0, 0, 1 }, { 0, 0, 0, 0 } });
    Tensor A = mat.dot(B);
    InitTest.svd(A.map(s -> Quantity.of(s, "V")));
    SingularValueDecomposition svd = InitTest.svd(A);
    assertEquals(MatrixRank.of(A), 3);
    Tensor nls = NullSpace.of(svd);
    Tensor nul = A.dot(nls.get(0));
    Tolerance.CHOP.requireAllZero(nul);
    VectorQ.requireLength(nul, n);
  }

  @Test
  void testSvd4() {
    Random random = new Random(1);
    int n = 11;
    Tensor mat = RandomVariate.of(NormalDistribution.standard(), random, n, n);
    SingularValueDecomposition svd = InitTest.svd(mat);
    Tolerance.CHOP.requireClose(PseudoInverse.of(svd), Inverse.of(mat)); // 1e-12 does not always work
    assertEquals(MatrixRank.of(mat), n);
    Tolerance.CHOP.requireClose(PseudoInverse.of(svd).dot(mat), IdentityMatrix.of(n));
  }

  @Test
  void testSvdR1() {
    Random random = new Random(1);
    int n = 15;
    Tensor matrix = Tensors.matrix((r, c) -> RationalScalar.of(random.nextInt(1000) - 500, random.nextInt(1000) + 1), n, n);
    SingularValueDecomposition svd = InitTest.svd(matrix);
    if (MatrixRank.of(matrix) == n) // 1e-12 failed in the past
      Chop._08.requireClose(PseudoInverse.of(svd), Inverse.of(matrix));
  }

  @Test
  void testSvdR2() {
    Random random = new Random(1);
    Tensor mat = Tensors.matrix((r, c) -> RationalScalar.of(random.nextInt(100) - 50, random.nextInt(100) + 1), 20, 4);
    Tensor B = Tensors.matrix(new Scalar[][] { //
        // "{1, 2, 3, -1}"
        { RationalScalar.of(1, 1), RationalScalar.of(2, 1), RationalScalar.of(3, 1), RationalScalar.of(-1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(4, 1), RationalScalar.of(2, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(1, 1) }, //
        { RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1), RationalScalar.of(0, 1) } });
    Tensor mat2 = mat.dot(B);
    InitTest.svd(mat.dot(B));
    assertEquals(MatrixRank.of(mat2), 3);
  }

  @Test
  void testSo3() {
    Tensor ad = LeviCivitaTensor.of(3);
    Tensor sk = ad.dot(Tensors.vector(1, 1, 1));
    InitTest.svd(sk);
    assertEquals(MatrixRank.of(sk), 2);
  }

  @Test
  void testFullConstant() {
    Tensor d = Tensors.matrix((i, j) -> RealScalar.of(1e-10), 10, 10);
    InitTest.svd(d);
    assertEquals(MatrixRank.of(d), 1);
  }

  @Test
  void testHilbert1() {
    Tensor d = HilbertMatrix.of(200, 20);
    InitTest.svd(d);
    assertEquals(20, MatrixRank.of(d));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 5, 10 })
  void testHilbert2(int rank) {
    Tensor matrix = HilbertMatrix.of(100, rank);
    InitTest.svd(matrix);
    assertEquals(rank, MatrixRank.of(matrix));
  }

  @Test
  void testJordan1() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1e-10, 1, 1, 1, 1e-10));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j, j + 1));
    InitTest.svd(d);
    assertEquals(MatrixRank.of(d), 5);
  }

  @Test
  void testJordan2() {
    Tensor d = DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 1));
    IntStream.range(0, 4).forEach(j -> d.set(RealScalar.of(1e-10), j + 1, j));
    InitTest.svd(d);
  }

  @Test
  void testScdR3() {
    int n = 10;
    int k = 8;
    Tensor mat = Array.zeros(n, n);
    mat.set(RationalScalar.of(1, 1), k - 4, k - 1);
    mat.set(RationalScalar.of(1, 1), k - 1, k - 4);
    SingularValueDecomposition svd = InitTest.svd(mat);
    assertEquals(MatrixRank.of(mat), 2);
    assertEquals(Sort.of(svd.values()), Tensors.fromString("{0, 0, 0, 0, 0, 0, 0, 0, 1.0, 1.0}"));
  }

  @Test
  void testEye() {
    assertEquals(MatrixRank.of(IdentityMatrix.of(10)), 10);
    assertEquals(MatrixRank.of(DiagonalMatrix.with(Tensors.vector(1, 1, 1, 1, 0, 0))), 4);
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> SingularValueDecomposition.of(RealScalar.ONE));
    assertThrows(Exception.class, () -> SingularValueDecomposition.of(Tensors.vector(1, 2, 3)));
    assertThrows(ClassCastException.class, () -> SingularValueDecomposition.of(Tensors.fromString("{{1, 2}, {2, {3}}}")));
    assertThrows(IllegalArgumentException.class, () -> SingularValueDecomposition.of(Array.zeros(2, 3, 4)));
  }

  @Test
  void testPackageVisibility() {
    assertTrue(Modifier.isPublic(SingularValueDecomposition.class.getModifiers()));
  }
}
