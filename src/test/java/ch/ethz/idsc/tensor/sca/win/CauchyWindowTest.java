// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class CauchyWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = CauchyWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.3790175864160096));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.28089887640449435));
  }
}
