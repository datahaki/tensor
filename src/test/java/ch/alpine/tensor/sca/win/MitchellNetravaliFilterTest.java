// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class MitchellNetravaliFilterTest extends TestCase {
  public void testSimple() {
    Scalar scalar = MitchellNetravaliFilter.FUNCTION.apply(RealScalar.of(0.1));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.6435555555555554));
  }

  public void testSpecial() {
    ScalarUnaryOperator scalarUnaryOperator = MitchellNetravaliFilter.of(0.5);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.ZERO);
    Tolerance.CHOP.requireClose(scalar, RealScalar.ONE);
  }
}
