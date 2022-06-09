// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class QRMathematicaTest {
  @Test
  public void testSkinny() {
    Tensor b = Transpose.of(IdentityMatrix.of(9).extract(0, 4));
    assertEquals(Dimensions.of(b), Arrays.asList(9, 4));
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 9, 4);
    QRDecompositionImpl qrDecompositionImpl = new QRDecompositionImpl(matrix, b, QRSignOperators.STABILITY);
    qrDecompositionImpl.getQConjugateTranspose();
    QRMathematica.wrap(QRDecomposition.of(matrix));
    // Chop._05.requireClose(qinv, wrap.getQ());
  }

  @Test
  public void testSimple() {
    Tensor a = Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
    assertEquals(Dimensions.of(a), Arrays.asList(4, 3));
    QRDecomposition qr = QRDecomposition.of(a);
    QRMathematica qrDecomposition = (QRMathematica) QRMathematica.wrap(qr);
    assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(3, 3));
    assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(4, 3));
    assertEquals(Dimensions.of(qrDecomposition.getQConjugateTranspose()), Arrays.asList(3, 4));
    Tensor r = qrDecomposition.getR();
    Tensor q = qrDecomposition.getQ();
    Tolerance.CHOP.requireClose(q.dot(r), a);
    Tolerance.CHOP.requireClose(ConjugateTranspose.of(q), qrDecomposition.getQConjugateTranspose());
    assertTrue(qrDecomposition.toString().startsWith("QRDecomposition["));
    QRDecompositionImpl qrdi = (QRDecompositionImpl) qr;
    Tolerance.CHOP.requireClose(qrdi.pseudoInverse(), qrDecomposition.pseudoInverse());
  }

  @Test
  public void testDet() {
    Distribution distribution = UniformDistribution.unit();
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      QRDecomposition qrDecomposition = QRMathematica.wrap(QRDecomposition.of(matrix));
      Tolerance.CHOP.requireClose(qrDecomposition.det(), Det.of(matrix));
    }
  }

  @Test
  public void testRectangularBigSmall() {
    Distribution distribution = NormalDistribution.standard();
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      QRDecompositionImpl qrdi = (QRDecompositionImpl) QRDecomposition.of(matrix);
      QRMathematica qrDecomposition = (QRMathematica) QRMathematica.wrap(qrdi);
      assertEquals(Dimensions.of(qrDecomposition.getQConjugateTranspose()), Arrays.asList(Math.min(n, m), n));
      List<Integer> dq = Dimensions.of(qrDecomposition.getQ());
      assertEquals(dq, Arrays.asList(n, Math.min(n, m)));
      assertEquals(dq, Dimensions.of(matrix));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(Math.min(n, m), m));
      SquareMatrixQ.require(qrDecomposition.getR());
      Tolerance.CHOP.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQConjugateTranspose()));
      Tolerance.CHOP.requireClose(qrdi.pseudoInverse(), qrDecomposition.pseudoInverse());
    }
  }

  @Test
  public void testRectangularSmallBig() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 3; n < 6; ++n) {
      int m = n + 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      QRDecompositionImpl qrdi = (QRDecompositionImpl) QRDecomposition.of(matrix);
      QRMathematica qrDecomposition = (QRMathematica) QRMathematica.wrap(qrdi);
      Tolerance.CHOP.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQ()));
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQConjugateTranspose()));
      assertEquals(Dimensions.of(qrDecomposition.getQConjugateTranspose()), Arrays.asList(Math.min(n, m), n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, Math.min(n, m)));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(Math.min(n, m), m));
      // qrdi.pseudoInverse();
      // Tolerance.CHOP.requireClose(qrdi.pseudoInverse(), qrDecomposition.pseudoInverse());
    }
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> QRMathematica.wrap(null));
  }
}
