// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class ConnesWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator connesWindow = ConnesWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.5625));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.3713378906250001));
  }
}
