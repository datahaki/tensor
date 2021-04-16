// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import java.io.IOException;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.LeastSquares;
import ch.ethz.idsc.tensor.mat.LowerTriangularize;
import ch.ethz.idsc.tensor.mat.MatrixRank;
import ch.ethz.idsc.tensor.mat.PseudoInverse;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.mat.re.Det;
import ch.ethz.idsc.tensor.pdf.ComplexNormalDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRDecompositionTest extends TestCase {
  private static QRDecomposition _specialOps(Tensor A) {
    QRDecomposition qrDecomposition = null;
    for (QRSignOperator qrSignOperator : QRSignOperators.values()) {
      qrDecomposition = QRDecomposition.of(A, qrSignOperator);
      Tensor Q = qrDecomposition.getQ();
      Tensor Qi = qrDecomposition.getQTranspose();
      Tensor R = qrDecomposition.getR();
      Chop._10.requireClose(Q.dot(R), A);
      Chop._10.requireClose(Q.dot(Qi), IdentityMatrix.of(A.length()));
      Scalar qrDet = Det.of(Q).multiply(Det.of(R));
      Chop._10.requireClose(qrDet, Det.of(A));
      Tensor lower = LowerTriangularize.of(R, -1);
      Chop.NONE.requireAllZero(lower);
      if (qrSignOperator.isDetExact()) {
        Chop._10.requireClose(qrDet, qrDecomposition.det());
      } else {
        assertTrue(Chop._10.isClose(qrDet, qrDecomposition.det()) || Chop._10.isClose(qrDet, qrDecomposition.det().negate()));
      }
    }
    return qrDecomposition;
  }

  public void testExampleP32() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { -1, -1, 1 }, //
        { 1, 3, 3 }, //
        { -1, -1, 5 }, //
        { 1, 3, 7 } });
    _specialOps(A);
  }

  public void testOnesMinusEye() {
    Tensor matrix = ConstantArray.of(RealScalar.ONE, 3, 3).subtract(IdentityMatrix.of(3));
    _specialOps(matrix);
  }

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
      AssertFail.of(() -> LeastSquares.usingQR(matrix, br));
      Tensor ls1 = LeastSquares.of(matrix, br);
      Tensor ls2 = PseudoInverse.of(matrix).dot(br);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
    {
      Tensor m = Transpose.of(matrix);
      Tensor b = RandomVariate.of(distribution, 4);
      AssertFail.of(() -> LeastSquares.usingQR(m, b));
      Tensor ls1 = LeastSquares.of(m, b);
      Tensor ls2 = PseudoInverse.of(m).dot(b);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
  }

  public void testRandomReal() {
    Tensor A = RandomVariate.of(UniformDistribution.unit(), 5, 3);
    _specialOps(A);
  }

  public void testRandomReal2() throws ClassNotFoundException, IOException {
    Tensor A = RandomVariate.of(UniformDistribution.unit(), 3, 5);
    QRDecomposition qrDecomposition = Serialization.copy(_specialOps(A));
    Chop.NONE.requireZero(qrDecomposition.det());
    ExactScalarQ.require(qrDecomposition.det());
  }

  public void testRandomRealSquare() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 1; d <= 10; ++d)
      _specialOps(RandomVariate.of(distribution, d, d));
  }

  public void testDiag() {
    Tensor A = DiagonalMatrix.with(Tensors.vector(2, 3, 4));
    _specialOps(A);
  }

  public void testDiag2() {
    Tensor A = DiagonalMatrix.of(2, -3, 0, 0, -1e-10, 0, 4e20);
    _specialOps(A);
  }

  public void testZeros() {
    Tensor A = Array.zeros(4, 3);
    _specialOps(A);
  }

  public void testRandomComplex1() {
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 5, 3));
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 3, 5));
  }

  public void testRandomComplex2() {
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 4, 4));
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 5, 5));
    _specialOps(RandomVariate.of(ComplexNormalDistribution.STANDARD, 6, 6));
  }

  public void testComplexDiagonal() {
    Tensor matrix = DiagonalMatrix.of(ComplexScalar.of(2, 3), ComplexScalar.of(-6, -1));
    _specialOps(matrix);
  }

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

  public void testQuantity() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12[s], -51[s], 4[s] }, { 6[s], 167[s], -68[s] }, { -4[s], 24[s], -41[s] } }");
    _specialOps(matrix);
    _specialOps(N.DOUBLE.of(matrix));
    QRDecomposition qr = QRDecomposition.of(matrix);
    assertTrue(qr.det() instanceof Quantity);
  }

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

  public void testMathematica1() {
    Tensor matrix = Tensors.fromString("{{1, 2}, {3, 4}, {5, 6}}");
    _specialOps(matrix);
    QRDecomposition qr = QRDecomposition.of(matrix, QRSignOperators.ORIENTATION);
    Tensor reference = Tensors.fromString("{5.916079783099616`, 0.828078671210825`}");
    Chop._10.requireClose(reference, Diagonal.of(qr.getR()));
    assertTrue(qr.toString().startsWith("QRDecomposition["));
  }

  public void testMathematica2() {
    Tensor matrix = Tensors.fromString("{{1., 2., 3.}, {4., 5., 6.}}");
    _specialOps(matrix);
  }

  public void testLower() {
    Tensor matrix = Tensors.matrixInt( //
        new int[][] { { 0, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 } });
    _specialOps(matrix);
  }

  public void testQuantityMixed() {
    Tensor matrix = Tensors.fromString( //
        "{{ 12[s], -51[A], 4[m] }, { 6[s], 167[A], -68[m] }, { -4[s], 24[A], -41[m] } }");
    _specialOps(matrix);
    _specialOps(N.DOUBLE.of(matrix));
    QRDecomposition qr = QRDecomposition.of(matrix);
    assertTrue(qr.det() instanceof Quantity);
  }

  public void testComplexMathematica() {
    Tensor matrix = Tensors.fromString("{{8 + I, 2 - 3 *I}, {3 + I, I}} ");
    _specialOps(matrix);
    _specialOps(N.DOUBLE.of(matrix));
  }

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

  public void testDet() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        _check(RandomVariate.of(distribution, d, d));
      }
  }

  public void testDetComplex() {
    Distribution distribution = ComplexNormalDistribution.STANDARD;
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        _check(RandomVariate.of(distribution, d, d));
      }
  }

  public void testPreserveOrientation() {
    Distribution distribution = NormalDistribution.standard();
    for (int d = 2; d < 5; ++d)
      for (int count = 0; count < 10; ++count) {
        Tensor matrix = RandomVariate.of(distribution, d, d);
        QRDecomposition qrDecomposition = QRDecomposition.of(matrix, QRSignOperators.ORIENTATION);
        assertEquals(Sign.FUNCTION.apply(Det.of(matrix)), Sign.FUNCTION.apply(Det.of(qrDecomposition.getQ())));
      }
  }

  public void testNullFail() {
    AssertFail.of(() -> QRDecomposition.of(IdentityMatrix.of(3), null));
  }
}
