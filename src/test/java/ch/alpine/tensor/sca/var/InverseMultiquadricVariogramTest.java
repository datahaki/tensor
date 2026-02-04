// code by jph
package ch.alpine.tensor.sca.var;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;

class InverseMultiquadricVariogramTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator scalarUnaryOperator = InverseMultiquadricVariogram.of(3);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(4));
    assertEquals(ExactScalarQ.require(scalar), RationalScalar.of(1, 5));
  }

  @Test
  void testNegativeFail() {
    assertThrows(Exception.class, () -> InverseMultiquadricVariogram.of(-3));
  }
}
