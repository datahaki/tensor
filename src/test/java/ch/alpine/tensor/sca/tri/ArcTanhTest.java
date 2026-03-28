// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Complex;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class ArcTanhTest {
  @Test
  void testReal() {
    Scalar scalar = ArcTanh.FUNCTION.apply(RealScalar.of(0.5));
    assertEquals(scalar, RealScalar.of(0.5493061443340548));
  }

  @Test
  void testComplex() {
    Scalar scalar = ArcTanh.FUNCTION.apply(Complex.of(5, -9));
    assertEquals(scalar, Complex.of(0.04686573907359337, -1.4859071898107274));
  }
}
