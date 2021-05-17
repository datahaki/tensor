// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class MitchellNetravaliWindowTest extends TestCase {
  public void testSimple() {
    Scalar scalar = MitchellNetravaliWindow.FUNCTION.apply(RealScalar.of(0.1));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.6435555555555554));
  }
}
