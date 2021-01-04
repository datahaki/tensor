// code by jph
package ch.ethz.idsc.tensor.lie;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.LeastSquares;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRDecompositionImplTest extends TestCase {
  public void testSolve() {
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 2);
      Tensor sol1 = qrDecomposition.solve(b);
      Tensor sol2 = LinearSolve.of(matrix, b);
      Chop._08.requireClose(sol1, sol2);
    }
  }

  public void testSolveComplex() {
    for (int n = 3; n < 6; ++n) {
      Tensor mr = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor mi = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor matrix = mr.add(mi.multiply(ComplexScalar.I));
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 3);
      Tensor sol1 = qrDecomposition.solve(b);
      Tensor sol2 = LinearSolve.of(matrix, b);
      Tolerance.CHOP.requireClose(sol1, sol2);
    }
  }

  public void testSolveLeastSquares() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 2);
      Tensor sol1 = qrDecomposition.solve(b);
      Tensor sol2 = LeastSquares.usingSvd(matrix, b);
      Tolerance.CHOP.requireClose(sol1, sol2);
    }
  }

  public void testSolveLeastSquaresVector() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n);
      Tensor sol1 = qrDecomposition.solve(b);
      Tensor sol2 = LeastSquares.usingSvd(matrix, b);
      Tolerance.CHOP.requireClose(sol1, sol2);
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
      Chop._07.requireClose(qrDecomposition.getQ().dot(qrDecomposition.getR()), matrix);
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getQ()));
      assertTrue(OrthogonalMatrixQ.of(qrDecomposition.getInverseQ()));
      assertEquals(Dimensions.of(qrDecomposition.getInverseQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getQ()), Arrays.asList(n, n));
      assertEquals(Dimensions.of(qrDecomposition.getR()), Arrays.asList(n, m));
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
