// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Sign;

class QRDecompositionTest {
  @Test
  void testExampleP32() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { -1, -1, 1 }, //
        { 1, 3, 3 }, //
        { -1, -1, 5 }, //
        { 1, 3, 7 } });
    QRDecompositionWrap.of(A);
  }

  @Test
  void testOnesMinusEye() {
    Tensor matrix = ConstantArray.of(RealScalar.ONE, 3, 3).subtract(IdentityMatrix.of(3));
    QRDecompositionWrap.of(matrix);
  }

  @Test
  void testRankDeficient() {
    int r = 2;
    Distribution distribution = NormalDistribution.standard();
    Tensor m1 = RandomVariate.of(distribution, 7, r);
    Tensor m2 = RandomVariate.of(distribution, r, 4);
    Tensor br = RandomVariate.of(distribution, 7);
    LeastSquares.usingQR(m1, br);
    Tensor matrix = m1.dot(m2);
    assertEquals(MatrixRank.of(matrix), r);
    QRDecompositionWrap.of(matrix);
    {
      assertThrows(Throw.class, () -> LeastSquares.usingQR(matrix, br));
      Tensor ls1 = LeastSquares.of(matrix, br);
      Tensor ls2 = PseudoInverse.of(matrix).dot(br);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
    {
      Tensor m = Transpose.of(matrix);
      Tensor b = RandomVariate.of(distribution, 4);
      assertThrows(Throw.class, () -> LeastSquares.usingQR(m, b));
      Tensor ls1 = LeastSquares.of(m, b);
      Tensor ls2 = PseudoInverse.of(m).dot(b);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
  }

  @ParameterizedTest
  @ValueSource(ints = { 3, 5, 10 })
  void testBigDecimal(int n) {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(50), random, n + 3, n);
    QRDecompositionWrap.of(matrix);
    // Tensor b = RandomVariate.of(DecimalRandomVariate.unit(50), random, n + 3, n+3);
    // QRDecompositionWrap.of(matrix, b, QRSignOperators.STABILITY);
  }

  @Test
  void testRandomReal() {
    Tensor A = RandomVariate.of(UniformDistribution.unit(), 5, 3);
    QRDecompositionWrap.of(A);
  }

  @Test
  void testRandomReal2() throws ClassNotFoundException, IOException {
    Tensor A = RandomVariate.of(UniformDistribution.unit(), 3, 5);
    QRDecomposition qrDecomposition = Serialization.copy(QRDecompositionWrap.of(A));
    Chop.NONE.requireZero(qrDecomposition.det());
    ExactScalarQ.require(qrDecomposition.det());
  }

  @Test
  void testRandomRealSquare() {
    Random random = new Random(3);
    Distribution distribution = NormalDistribution.standard();
    for (int d = 1; d <= 10; ++d)
      QRDecompositionWrap.of(RandomVariate.of(distribution, random, d, d));
  }

  @Test
  void testDiag() {
    Tensor A = DiagonalMatrix.with(Tensors.vector(2, 3, 4));
    QRDecompositionWrap.of(A);
  }

  @Test
  void testDiag2() {
    Tensor A = DiagonalMatrix.of(2, -3, 0, 0, -1e-10, 0, 4e20);
    QRDecompositionWrap.of(A);
  }

  @Test
  void testZeros() {
    Tensor A = Array.zeros(4, 3);
    QRDecompositionWrap.of(A);
  }

  @Test
  void testRandomComplex1() {
    QRDecompositionWrap.of(RandomVariate.of(ComplexNormalDistribution.STANDARD, 5, 3));
    QRDecompositionWrap.of(RandomVariate.of(ComplexNormalDistribution.STANDARD, 3, 5));
  }

  @Test
  void testRandomComplex2() {
    QRDecompositionWrap.of(RandomVariate.of(ComplexNormalDistribution.STANDARD, 4, 4));
    QRDecompositionWrap.of(RandomVariate.of(ComplexNormalDistribution.STANDARD, 5, 5));
    QRDecompositionWrap.of(RandomVariate.of(ComplexNormalDistribution.STANDARD, 6, 6));
  }

  @Test
  void testComplexDiagonal() {
    Tensor matrix = DiagonalMatrix.of(ComplexScalar.of(2, 3), ComplexScalar.of(-6, -1));
    QRDecompositionWrap.of(matrix);
  }

  @Test
  void testHilbert() {
    Tensor matrix = HilbertMatrix.of(4, 7);
    QRDecomposition qr = QRDecompositionWrap.of(matrix);
    assertEquals(qr.getR().get(1, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(2, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(2, 1), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 1), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 2), RealScalar.ZERO);
  }

  @Test
  void testQuantity() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12[s], -51[s], 4[s] }, { 6[s], 167[s], -68[s] }, { -4[s], 24[s], -41[s] } }");
    QRDecomposition qr = QRDecompositionWrap.of(matrix);
    QRDecompositionWrap.of(matrix.maps(N.DOUBLE));
    assertInstanceOf(Quantity.class, qr.det());
  }

  @Test
  void testWikipedia() {
    Tensor matrix = Tensors.matrixInt( //
        new int[][] { { 12, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 } });
    QRDecomposition qr = QRDecompositionWrap.of(matrix);
    Tensor getR = Tensors.matrixInt( //
        new int[][] { { 14, 21, -14 }, { 0, 175, -70 }, { 0, 0, -35 } });
    assertEquals(getR, qr.getR());
    ExactTensorQ.require(qr.getR());
    ExactTensorQ.require(qr.getQ());
    assertTrue(Flatten.of(qr.getR()).stream().map(Scalar.class::cast).allMatch(ExactScalarQ::of));
    assertTrue(Flatten.of(qr.getQ()).stream().map(Scalar.class::cast).allMatch(ExactScalarQ::of));
  }

  @Test
  void testMathematica1() {
    Tensor matrix = Tensors.fromString("{{1, 2}, {3, 4}, {5, 6}}");
    QRDecomposition qr = QRDecompositionWrap.of(matrix, QRSignOperators.ORIENTATION);
    Tensor reference = Tensors.fromString("{5.916079783099616`, 0.828078671210825`}");
    Chop._10.requireClose(reference, Diagonal.of(qr.getR()));
    assertTrue(qr.toString().startsWith("QRDecomposition["));
  }

  @Test
  void testMathematica2() {
    Tensor matrix = Tensors.fromString("{{1., 2., 3.}, {4., 5., 6.}}");
    QRDecompositionWrap.of(matrix);
  }

  @Test
  void testLower() {
    Tensor matrix = Tensors.matrixInt( //
        new int[][] { { 0, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 } });
    QRDecompositionWrap.of(matrix);
  }

  @Test
  void testQuantityMixed() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12[s], -51[A], 4[m] }, { 6[s], 167[A], -68[m] }, { -4[s], 24[A], -41[m] } }");
    QRDecompositionWrap.of(matrix.maps(N.DOUBLE));
    QRDecomposition qr = QRDecompositionWrap.of(matrix);
    assertInstanceOf(Quantity.class, qr.det());
  }

  @Test
  void testComplexMathematica() {
    Tensor matrix = Tensors.fromString("{{8 + I, 2 - 3 *I}, {3 + I, I}} ");
    QRDecompositionWrap.of(matrix);
    QRDecompositionWrap.of(matrix.maps(N.DOUBLE));
  }

  @Test
  void testQuantityComplex() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12+3*I[s], -51[A], 4[m] }, { 6[s], 167-7*I[A], -68[m] }, { -4*I[s], 24[A], -41-9*I[m] } }");
    QRDecompositionWrap.of(matrix);
    QRDecompositionWrap.of(matrix.maps(N.DOUBLE));
  }

  private static void _check(Tensor matrix) {
    for (QRSignOperator qrSignOperator : QRSignOperators.values()) {
      QRDecomposition qrDecomposition = QRDecompositionWrap.of(matrix, qrSignOperator);
      Tensor q = qrDecomposition.getQ();
      Tensor r = qrDecomposition.getR();
      Scalar d1 = Det.of(matrix);
      Scalar d2 = qrDecomposition.det();
      if (qrSignOperator.isDetExact())
        Chop._08.requireClose(d1, d2);
      else {
        assertTrue(Chop._08.isClose(d1, d2) || Chop._08.isClose(d1, d2.negate()));
      }
      Chop._08.requireClose(q.dot(r), matrix);
    }
  }

  @Test
  void testDet() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        _check(RandomVariate.of(distribution, d, d));
      }
  }

  @Test
  void testDetComplex() {
    Distribution distribution = ComplexNormalDistribution.STANDARD;
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        _check(RandomVariate.of(distribution, d, d));
      }
  }

  @Test
  void testPreserveOrientation() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        Tensor matrix = RandomVariate.of(distribution, d, d);
        QRDecomposition qrDecomposition = QRDecompositionWrap.of(matrix, QRSignOperators.ORIENTATION);
        assertEquals(Sign.FUNCTION.apply(Det.of(matrix)), Sign.FUNCTION.apply(Det.of(qrDecomposition.getQ())));
      }
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> QRDecomposition.of(IdentityMatrix.of(3), null));
  }
}
