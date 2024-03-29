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
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;

class SinTest {
  @Test
  void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Sin.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.sin(2));
    assertEquals(c, Sin.FUNCTION.apply(i));
    assertEquals(c, s);
  }

  @Test
  void testComplex() {
    Scalar c = Sin.FUNCTION.apply(ComplexScalar.of(2, 3.));
    Scalar s = ComplexScalar.of(9.15449914691143, -4.168906959966565);
    assertEquals(c, s);
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> Sin.FUNCTION.apply(Quantity.of(1, "deg")));
  }

  @Test
  void testStringScalarFail() {
    assertThrows(Throw.class, () -> Sin.FUNCTION.apply(StringScalar.of("some")));
  }
}
