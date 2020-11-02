// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Variance;
import junit.framework.TestCase;

public class StudentTDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(StudentTDistribution.of(2, 3, 5));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose( //
        pdf.at(RealScalar.of(1.75)), //
        RealScalar.of(0.1260097929094335));
    assertEquals(distribution.toString(), "StudentTDistribution[2, 3, 5]");
  }

  public void testMeanVar() {
    Distribution distribution = StudentTDistribution.of(5, 4, 3);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(5));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), RealScalar.of(48));
  }

  public void testMeanVarSpecial() {
    assertFalse(DeterminateScalarQ.of(Mean.of(StudentTDistribution.of(5, 4, 0.5))));
    assertFalse(DeterminateScalarQ.of(Variance.of(StudentTDistribution.of(5, 4, 1.5))));
  }
}
