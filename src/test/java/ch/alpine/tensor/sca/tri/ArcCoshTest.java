// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Complex;
import ch.alpine.tensor.Scalar;

class ArcCoshTest {
  @Test
  void testArcCosh() {
    Scalar s = Complex.of(5, -7);
    Scalar r = ArcCosh.FUNCTION.apply(s);
    Scalar a = Complex.of(2.8462888282083862, -0.9537320301189031);
    assertEquals(a, r);
    assertEquals(a, ArcCosh.FUNCTION.apply(s));
  }
}
