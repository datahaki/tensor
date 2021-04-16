// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRMathematicaTest extends TestCase {
  public void testSkinny() {
    Tensor b = Transpose.of(IdentityMatrix.of(9).extract(0, 4));
    assertEquals(Dimensions.of(b), Arrays.asList(9, 4));
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 9, 4);
    QRDecompositionImpl qrDecompositionImpl = new QRDecompositionImpl(matrix, b, QRSignOperators.STABILITY);
    qrDecompositionImpl.getInverseQ();
    QRMathematica.wrap(QRDecomposition.of(matrix));
    // Chop._05.requireClose(qinv, wrap.getQ());
  }

  public void testSimple() {
    Tensor a = Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
    assertEquals(Dimensions.of(a), Arrays.asList(4, 3));
    QRDecomposition qr = QRDecomposition.of(a);
    QRMathematica qrDecomposition = (QRMathematica) QRMathematica.wrap(qr);
    assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(3, 3));
    assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(4, 3));
    assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(3, 4));
    Tensor r = qrDecomposition.getR();
    Tensor q = qrDecomposition.getQ();
    Tolerance.CHOP.requireClose(q.dot(r), a);
    Tolerance.CHOP.requireClose(ConjugateTranspose.of(q), qrDecomposition.getInverseQ());
    assertTrue(qrDecomposition.toString().startsWith("QRDecomposition["));
    QRDecompositionImpl qrdi = (QRDecompositionImpl) qr;
    Tolerance.CHOP.requireClose(qrdi.pseudoInverse(), qrDecomposition.pseudoInverse());
  }

  public void testDet() {
    Distribution distribution = UniformDistribution.unit();
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      QRDecomposition qrDecomposition = QRMathematica.wrap(QRDecomposition.of(matrix));
      Tolerance.CHOP.requireClose(qrDecomposition.det(), Det.of(matrix));
    }
  }

  public void testRectangularBigSmall() {
    Distribution distribution = NormalDistribution.standard();
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      QRDecompositionImpl qrdi = (QRDecompositionImpl) QRDecomposition.of(matrix);
      QRMathematica qrDecomposition = (QRMathematica) QRMathematica.wrap(qrdi);
      assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(Math.min(n, m), n));
      List<Integer> dq = Dimensions.of(qrDecomposition.getQ());
      assertEquals(dq, Arrays.asList(n, Math.min(n, m)));
      assertEquals(dq, Dimensions.of(matrix));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(Math.min(n, m), m));
      SquareMatrixQ.require(qrDecomposition.getR());
      Tolerance.CHOP.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getInverseQ()));
      Tolerance.CHOP.requireClose(qrdi.pseudoInverse(), qrDecomposition.pseudoInverse());
    }
  }

  public void testRectangularSmallBig() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 3; n < 6; ++n) {
      int m = n + 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      QRDecompositionImpl qrdi = (QRDecompositionImpl) QRDecomposition.of(matrix);
      QRMathematica qrDecomposition = (QRMathematica) QRMathematica.wrap(qrdi);
      Tolerance.CHOP.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQ()));
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getInverseQ()));
      assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(Math.min(n, m), n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, Math.min(n, m)));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(Math.min(n, m), m));
      // qrdi.pseudoInverse();
      // Tolerance.CHOP.requireClose(qrdi.pseudoInverse(), qrDecomposition.pseudoInverse());
    }
  }

  public void testNullFail() {
    AssertFail.of(() -> QRMathematica.wrap(null));
  }
}
