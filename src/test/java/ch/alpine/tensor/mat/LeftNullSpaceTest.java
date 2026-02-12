// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.mat.pd.Orthogonalize;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

class LeftNullSpaceTest {
  @Test
  void testRankDeficient() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {0, 1}, {0, 1}, {0, 1}}");
    Tensor nullsp = LeftNullSpace.usingRowReduce(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(3, 4));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  @Test
  void testMaxRank() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {2, 1}, {0, 1}, {0, 1}}");
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(2, 4));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  @Test
  void testRankDeficientTranspose() {
    Tensor matrix = Tensors.fromString("{{0, 0, 0, 0}, {1, 1, 1, 1}}");
    Tensor nullsp = LeftNullSpace.usingRowReduce(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(1, 2));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  @Test
  void testMaxRankTranspose() {
    Tensor matrix = Tensors.fromString("{{0, 2, 0, 0}, {1, 1, 1, 1}}");
    Tensor nullsp = LeftNullSpace.usingRowReduce(matrix);
    assertEquals(Dimensions.of(nullsp), List.of(0));
  }

  private static void _matrix(Tensor A) {
    assertTrue(NullSpace.of(A).stream().map(A::dot).allMatch(Chop.NONE::allZero));
    assertTrue(LeftNullSpace.usingRowReduce(A).stream().map(vector -> vector.dot(A)).allMatch(Chop.NONE::allZero));
    _matrixNumeric(A);
  }

  private static void _matrixNumeric(Tensor A) {
    Chop chop = Chop._08;
    chop.requireAllZero(Tensor.of(NullSpace.of(A.maps(N.DOUBLE)).stream().map(A::dot)));
    chop.requireAllZero(Tensor.of(NullSpace.usingQR(A.maps(N.DOUBLE)).stream().map(A::dot)));
  }

  private static void _check(Tensor A) {
    _matrix(A);
    _matrix(Transpose.of(A));
  }

  @ParameterizedTest
  @MethodSource(value = "test.bulk.TestDistributions#distributions")
  void testRandomSome(Distribution distribution) {
    Tensor matrix = RandomVariate.of(distribution, 7, 3);
    Tensor ns = LeftNullSpace.of(matrix);
    assertEquals(List.of(4, 7), Dimensions.of(ns));
  }

  @Test
  void testBulk() {
    _check(Tensors.fromString("{{0, 0}}"));
    _check(Tensors.fromString("{{0, 1}}"));
    _check(Tensors.fromString("{{1, 0, 3}}"));
    _check(Tensors.fromString("{{1[m], 0[m], 3[m]}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 2}, {1, 0}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 3}, {1, 0}, {0, 0}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 0}, {0, 0}, {0, 0}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 0}, {1, 0}, {0, 0}}"));
    _check(Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {1, 0, 0}}").maps(s -> Quantity.of(s, "m")));
    _check(Tensors.fromString("{{0, 0, 0}, {0, 0, 0}, {1, 0, 0}, {3, 2, 0}}").maps(s -> Quantity.of(s, "m")));
    _check(Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}}").maps(s -> Quantity.of(s, "m")));
    _check(Tensors.fromString("{{0, 5, 1}, {0, 0, 0}, {1, 0, 0}, {3, 2, 0}}").maps(s -> Quantity.of(s, "m")));
  }

  @ParameterizedTest
  @MethodSource(value = "test.bulk.TestDistributions#distributions")
  void testRandom(Distribution distribution) {
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 7);
    _matrixNumeric(TensorProduct.of(x, y));
    _matrixNumeric(TensorProduct.of(y, x));
  }

  @Test
  void testGaussScalar() {
    RandomGenerator random = ThreadLocalRandom.current();
    int prime = 7741;
    int dim1 = 6;
    Tensor matrix = Array.fill(() -> GaussScalar.of(random.nextInt(), prime), dim1 - 2, dim1);
    Tensor identi = DiagonalMatrix.of(Unprotect.dimension1(matrix), GaussScalar.of(1, prime));
    List<Integer> list = Dimensions.of(identi);
    assertEquals(list, Arrays.asList(dim1, dim1));
    Tensor nullsp = NullSpace.usingRowReduce(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(2, 6));
    assumeTrue(SquareMatrixQ.INSTANCE.test(matrix));
    Scalar det = Det.of(matrix);
    assertEquals(det, GaussScalar.of(0, prime));
  }

  @Test
  void testRectangle3x2G() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0}, {0, 0}, {0, 0}}").maps(suo);
    Tensor tensor = NullSpace.usingRowReduce(matrix);
    assertEquals(tensor.get(0), UnitVector.of(2, 1).maps(suo));
  }

  @Test
  void testRectangle2x3G() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0, 0}, {0, 0, 0}}").maps(suo);
    Tensor tensor = NullSpace.usingRowReduce(matrix);
    assertEquals(tensor.get(0), UnitVector.of(3, 1).maps(suo));
    assertEquals(tensor.get(1), UnitVector.of(3, 2).maps(suo));
  }

  @Test
  void testLeftGaussScalar() {
    int prime = 7879;
    RandomGenerator random = ThreadLocalRandom.current();
    Tensor matrix = Tensors.matrix((_, _) -> GaussScalar.of(random.nextInt(), prime), 7, 4);
    Tensor nullsp = LeftNullSpace.usingRowReduce(matrix);
    assertEquals(nullsp.length(), 3);
    for (Tensor vector : nullsp)
      Chop.NONE.requireAllZero(Dot.of(vector, matrix));
  }

  @RepeatedTest(5)
  void testLeftNullSpaceSingular(RepetitionInfo repetitionInfo) {
    int r = 2;
    int n = 5;
    int c = r + repetitionInfo.getCurrentRepetition();
    Tensor v = RandomVariate.of(NormalDistribution.standard(), r, n);
    Tensor w = RandomVariate.of(NormalDistribution.standard(), c, r);
    Tensor matrix = w.dot(v);
    QRDecomposition qrDecomposition = GramSchmidt.of(ConjugateTranspose.of(matrix));
    int rank = qrDecomposition.getR().length();
    assertEquals(rank, r);
    Tensor augm = Join.of(qrDecomposition.getR(), IdentityMatrix.of(c));
    Tensor frame = Orthogonalize.of(augm).extract(r, c);
    Tensor verify = frame.dot(matrix);
    Tolerance.CHOP.requireAllZero(verify);
  }

  @RepeatedTest(5)
  void testLeftNullSpaceComplex(RepetitionInfo repetitionInfo) {
    int r = 2;
    int n = 5;
    int c = r + repetitionInfo.getCurrentRepetition();
    Tensor v = RandomVariate.of(ComplexNormalDistribution.STANDARD, r, n);
    Tensor w = RandomVariate.of(ComplexNormalDistribution.STANDARD, c, r);
    Tensor matrix = w.dot(v);
    QRDecomposition qrDecomposition = GramSchmidt.of(ConjugateTranspose.of(matrix));
    int rank = qrDecomposition.getR().length();
    assertEquals(rank, r);
    Tensor augm = Join.of(qrDecomposition.getR(), IdentityMatrix.of(c));
    Tensor frame = Orthogonalize.of(augm).extract(r, c);
    Tensor verify = frame.dot(matrix);
    Tolerance.CHOP.requireAllZero(verify);
    // Tolerance.CHOP.requireAllZero(LeftNullSpace.of(matrix).dot(matrix));
  }
}
