// code by jph
package ch.ethz.idsc.tensor.mat;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRDecompositionImplTest extends TestCase {
  public void testDegenerate0Square() {
    for (int n = 1; n < 6; ++n) {
      QRDecomposition qrDecomposition = QRDecomposition.of(Array.zeros(n, n));
      assertEquals(qrDecomposition.getInverseQ(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), Array.zeros(n, n));
    }
  }

  public void testDegenerate1Square() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = DiagonalMatrix.with(UnitVector.of(n, 0));
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      assertEquals(qrDecomposition.getInverseQ(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), matrix);
    }
  }

  public void testDegenerateRect() {
    for (int m = 1; m < 6; ++m) {
      int n = m + 2;
      QRDecomposition qrDecomposition = QRDecomposition.of(Array.zeros(n, m));
      assertEquals(qrDecomposition.getInverseQ(), IdentityMatrix.of(n));
      assertEquals(qrDecomposition.getR(), Array.zeros(n, m));
    }
  }

  public void testDimensionsBigSmall() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(n, m));
    }
  }

  public void testSmallBigVector() {
    for (int m = 4; m < 7; ++m) {
      int n = m - 2;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      Chop._10.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQ()));
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getInverseQ()));
      assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(n, m));
    }
  }

  public void testPseudoInverseIdempotent() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      Tensor sol = LeastSquares.usingQR(matrix, IdentityMatrix.of(n));
      assertEquals(Dimensions.of(sol), Arrays.asList(m, n));
      Chop._10.requireClose(PseudoInverse.usingSvd(matrix), sol);
    }
  }

  public void testPseudoInverseRankDeficient() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor base = RandomVariate.of(NormalDistribution.standard(), n, m);
      Tensor mult = RandomVariate.of(NormalDistribution.standard(), m, m + 2);
      Tensor matrix = Dot.of(base, mult);
      QRDecompositionImpl qrDecompositionImpl = //
          new QRDecompositionImpl(matrix, IdentityMatrix.of(n), QRSignOperators.STABILITY);
      AssertFail.of(() -> qrDecompositionImpl.pseudoInverse(Tolerance.CHOP));
    }
  }

  public void testEmpty() {
    AssertFail.of(() -> QRDecomposition.of(Tensors.empty()));
  }

  public void testFail() {
    AssertFail.of(() -> QRDecomposition.of(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    AssertFail.of(() -> QRDecomposition.of(LeviCivitaTensor.of(3)));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(QRDecompositionImpl.class.getModifiers()));
  }
}
