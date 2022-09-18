// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.Chop;

class TanhTest {
  @Test
  void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Tanh.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.tanh(2));
    assertEquals(c, Tanh.of(i));
    assertEquals(c, s);
  }

  @Test
  void testComplex() {
    Scalar c = Tanh.of(ComplexScalar.of(2, 3.));
    Scalar s = ComplexScalar.of(0.965385879022133, -0.009884375038322494);
    Chop._13.requireClose(c, s);
  }

  @Test
  void testFail() {
    Scalar scalar = GaussScalar.of(3, 11);
    assertThrows(Throw.class, () -> Tanh.of(scalar));
  }
}
