// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;

class MitchellNetravaliFilterTest {
  @Test
  void testSimple() {
    Scalar scalar = MitchellNetravaliFilter.FUNCTION.apply(RealScalar.of(0.1));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.6435555555555554));
  }

  @Test
  void testSpecial() {
    ScalarUnaryOperator scalarUnaryOperator = MitchellNetravaliFilter.of(0.5);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.ZERO);
    Tolerance.CHOP.requireClose(scalar, RealScalar.ONE);
    assertTrue(scalarUnaryOperator.toString().startsWith("MitchellNetravaliFilter["));
  }
}
