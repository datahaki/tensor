// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class StudentTDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(StudentTDistribution.of(2, 3, 5));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose( //
        pdf.at(RealScalar.of(1.75)), //
        RealScalar.of(0.1260097929094335));
  }
}
