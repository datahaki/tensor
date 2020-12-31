// code by jph
package ch.ethz.idsc.tensor.lie;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.LinearSolve;
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
      Chop._06.requireClose(sol1, sol2);
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
      Chop._10.requireClose(sol1, sol2);
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
