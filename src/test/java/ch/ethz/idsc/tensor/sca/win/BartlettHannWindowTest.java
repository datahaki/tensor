// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class BartlettHannWindowTest extends TestCase {
  public void testSimple() {
    Tolerance.CHOP.requireClose( //
        BartlettHannWindow.FUNCTION.apply(RealScalar.of(0.3)), //
        RealScalar.of(0.35857354213752));
    Tolerance.CHOP.requireClose( //
        BartlettHannWindow.FUNCTION.apply(RealScalar.of(0.5)), //
        RealScalar.of(0));
  }
}
