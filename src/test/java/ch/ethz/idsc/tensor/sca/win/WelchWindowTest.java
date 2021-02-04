// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class WelchWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = WelchWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.75));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.6093750000000001));
  }

  public void testNullFail() {
    AssertFail.of(() -> WelchWindow.of(null));
  }
}
