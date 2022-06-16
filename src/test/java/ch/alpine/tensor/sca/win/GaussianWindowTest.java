// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;

class GaussianWindowTest {
  @Test
  void testSimple() {
    Scalar apply = GaussianWindow.FUNCTION.apply(RealScalar.of(0.2));
    Scalar exact = RealScalar.of(0.80073740291680804078);
    Tolerance.CHOP.requireClose(apply, exact);
  }

  @Test
  void testOutside() throws ClassNotFoundException, IOException {
    Scalar scalar = Serialization.copy(GaussianWindow.FUNCTION).apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
    assertEquals(GaussianWindow.FUNCTION.toString(), "GaussianWindow[3/10]");
  }

  @Test
  void testCustom() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator copy = Serialization.copy(GaussianWindow.of(RationalScalar.of(2, 10)));
    Scalar apply = copy.apply(RealScalar.of(0.4));
    Scalar exact = RealScalar.of(0.13533528323661262);
    Tolerance.CHOP.requireClose(apply, exact);
  }

  @Test
  void testToString() {
    assertEquals(GaussianWindow.FUNCTION.toString(), "GaussianWindow[3/10]");
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> GaussianWindow.of(null));
  }
}
