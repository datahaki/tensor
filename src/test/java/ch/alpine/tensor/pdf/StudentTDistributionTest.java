// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.DeterminateScalarQ;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
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
