// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
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
import ch.alpine.tensor.mat.LowerTriangularize;
import ch.alpine.tensor.mat.SquareMatrixQ;
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

public class QRDecompositionTest {
  private static QRDecomposition _specialOps(Tensor A) {
    QRDecomposition qrDecomposition = null;
    for (QRSignOperator qrSignOperator : QRSignOperators.values()) {
      qrDecomposition = QRDecomposition.of(A, qrSignOperator);
      Tensor Q = qrDecomposition.getQ();
      Tensor Qi = qrDecomposition.getQConjugateTranspose();
      Tensor R = qrDecomposition.getR();
      Chop._10.requireClose(Q.dot(R), A);
      Chop._10.requireClose(Q.dot(Qi), IdentityMatrix.of(A.length()));
      Scalar detR = Diagonal.of(R).stream().map(Scalar.class::cast).reduce(Scalar::multiply).get();
      Scalar qrDet = Det.of(Q).multiply(detR);
      if (SquareMatrixQ.of(A)) {
        Scalar detA = Det.of(A);
        Chop._10.requireClose(qrDet, detA);
        Tensor lower = LowerTriangularize.of(R, -1);
        Chop.NONE.requireAllZero(lower);
        if (qrSignOperator.isDetExact()) {
          Chop._10.requireClose(qrDet, qrDecomposition.det());
        } else {
          assertTrue(Chop._10.isClose(qrDet, qrDecomposition.det()) || Chop._10.isClose(qrDet, qrDecomposition.det().negate()));
        }
      }
    }
    return qrDecomposition;
  }

  @Test
  public void testExampleP32() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { -1, -1, 1 }, //
        { 1, 3, 3 }, //
        { -1, -1, 5 }, //
        { 1, 3, 7 } });
    _specialOps(A);
  }

  @Test
  public void testOnesMinusEye() {
    Tensor matrix = ConstantArray.of(RealScalar.ONE, 3, 3).subtract(IdentityMatrix.of(3));
    _specialOps(matrix);
  }

  @Test
  public void testRankDeficient() {
    int r = 2;
    Distribution distribution = NormalDistribution.standard();
    Tensor m1 = RandomVariate.of(distribution, 7, r);
    Tensor m2 = RandomVariate.of(distribution, r, 4);
    Tensor br = RandomVariate.of(distribution, 7);
    LeastSquares.usingQR(m1, br);
    Tensor matrix = m1.dot(m2);
    assertEquals(MatrixRank.of(matrix), r);
    _specialOps(matrix);
    {
      assertThrows(TensorRuntimeException.class, () -> LeastSquares.usingQR(matrix, br));
      Tensor ls1 = LeastSquares.of(matrix, br);
      Tensor ls2 = PseudoInverse.of(matrix).dot(br);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
    {
      Tensor m = Transpose.of(matrix);
      Tensor b = RandomVariate.of(distribution, 4);
      assertThrows(TensorRuntimeException.class, () -> LeastSquares.usingQR(m, b));
      Tensor ls1 = LeastSquares.of(m, b);
      Tensor ls2 = PseudoInverse.of(m).dot(b);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
  }

  @Test
  public void testRandomReal() {
    Tensor A = RandomVariate.of(UniformDistribution.unit(), 5, 3);
    _specialOps(A);
  }

  @Test
  public void testRandomReal2() throws ClassNotFoundException, IOException {
    Tensor A = RandomVariate.of(UniformDistribution.unit(), 3, 5);
    QRDecomposition qrDecomposition = Serialization.copy(_specialOps(A));
    Chop.NONE.requireZero(qrDecomposition.det());
    ExactScalarQ.require(qrDecomposition.det());
  }

  @Test
  public void testRandomRealSquare() {
    Random random = new Random(3);
    Distribution distribution = NormalDistribution.standard();
    for (int d = 1; d <= 10; ++d)
      _specialOps(RandomVariate.of(distribution, random, d, d));
  }

  @Test
  public void testDiag() {
    Tensor A = DiagonalMatrix.with(Tensors.vector(2, 3, 4));
    _specialOps(A);
  }

  @Test
  public void testDiag2() {
    Tensor A = DiagonalMatrix.of(2, -3, 0, 0, -1e-10, 0, 4e20);
    _specialOps(A);
  }

  @Test
  public void testZeros() {
    Tensor A = Array.zeros(4, 3);
    _specialOps(A);
  }

  @Test
  public void testRandomComplex1() {
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 5, 3));
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 3, 5));
  }

  @Test
  public void testRandomComplex2() {
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 4, 4));
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 5, 5));
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 6, 6));
  }

  @Test
  public void testComplexDiagonal() {
    Tensor matrix = DiagonalMatrix.of(ComplexScalar.of(2, 3), ComplexScalar.of(-6, -1));
    _specialOps(matrix);
  }

  @Test
  public void testHilbert() {
    Tensor matrix = HilbertMatrix.of(4, 7);
    _specialOps(matrix);
    QRDecomposition qr = QRDecomposition.of(matrix);
    assertEquals(qr.getR().get(1, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(2, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(2, 1), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 0), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 1), RealScalar.ZERO);
    assertEquals(qr.getR().get(3, 2), RealScalar.ZERO);
  }

  @Test
  public void testQuantity() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12[s], -51[s], 4[s] }, { 6[s], 167[s], -68[s] }, { -4[s], 24[s], -41[s] } }");
    _specialOps(matrix);
    _specialOps(N.DOUBLE.of(matrix));
    QRDecomposition qr = QRDecomposition.of(matrix);
    assertTrue(qr.det() instanceof Quantity);
  }

  @Test
  public void testWikipedia() {
    Tensor matrix = Tensors.matrixInt( //
        new int[][] { { 12, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 } });
    _specialOps(matrix);
    QRDecomposition qr = QRDecomposition.of(matrix);
    Tensor getR = Tensors.matrixInt( //
        new int[][] { { 14, 21, -14 }, { 0, 175, -70 }, { 0, 0, -35 } });
    assertEquals(getR, qr.getR());
    ExactTensorQ.require(qr.getR());
    ExactTensorQ.require(qr.getQ());
    assertTrue(Flatten.of(qr.getR()).stream().map(Scalar.class::cast).allMatch(ExactScalarQ::of));
    assertTrue(Flatten.of(qr.getQ()).stream().map(Scalar.class::cast).allMatch(ExactScalarQ::of));
  }

  @Test
  public void testMathematica1() {
    Tensor matrix = Tensors.fromString("{{1, 2}, {3, 4}, {5, 6}}");
    _specialOps(matrix);
    QRDecomposition qr = QRDecomposition.of(matrix, QRSignOperators.ORIENTATION);
    Tensor reference = Tensors.fromString("{5.916079783099616`, 0.828078671210825`}");
    Chop._10.requireClose(reference, Diagonal.of(qr.getR()));
    assertTrue(qr.toString().startsWith("QRDecomposition["));
  }

  @Test
  public void testMathematica2() {
    Tensor matrix = Tensors.fromString("{{1., 2., 3.}, {4., 5., 6.}}");
    _specialOps(matrix);
  }

  @Test
  public void testLower() {
    Tensor matrix = Tensors.matrixInt( //
        new int[][] { { 0, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 } });
    _specialOps(matrix);
  }

  @Test
  public void testQuantityMixed() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12[s], -51[A], 4[m] }, { 6[s], 167[A], -68[m] }, { -4[s], 24[A], -41[m] } }");
    _specialOps(matrix);
    _specialOps(N.DOUBLE.of(matrix));
    QRDecomposition qr = QRDecomposition.of(matrix);
    assertTrue(qr.det() instanceof Quantity);
  }

  @Test
  public void testComplexMathematica() {
    Tensor matrix = Tensors.fromString("{{8 + I, 2 - 3 *I}, {3 + I, I}} ");
    _specialOps(matrix);
    _specialOps(N.DOUBLE.of(matrix));
  }

  @Test
  public void testQuantityComplex() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12+3*I[s], -51[A], 4[m] }, { 6[s], 167-7*I[A], -68[m] }, { -4*I[s], 24[A], -41-9*I[m] } }");
    _specialOps(matrix);
    _specialOps(N.DOUBLE.of(matrix));
  }

  private static void _check(Tensor matrix) {
    for (QRSignOperator qrSignOperator : QRSignOperators.values()) {
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix, qrSignOperator);
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
  public void testDet() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        _check(RandomVariate.of(distribution, d, d));
      }
  }

  @Test
  public void testDetComplex() {
    Distribution distribution = ComplexNormalDistribution.STANDARD;
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        _check(RandomVariate.of(distribution, d, d));
      }
  }

  @Test
  public void testPreserveOrientation() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        Tensor matrix = RandomVariate.of(distribution, d, d);
        QRDecomposition qrDecomposition = QRDecomposition.of(matrix, QRSignOperators.ORIENTATION);
        assertEquals(Sign.FUNCTION.apply(Det.of(matrix)), Sign.FUNCTION.apply(Det.of(qrDecomposition.getQ())));
      }
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> QRDecomposition.of(IdentityMatrix.of(3), null));
  }
}
