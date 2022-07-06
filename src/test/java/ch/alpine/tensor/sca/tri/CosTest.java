// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;

class CosTest {
  @Test
  void testReal() {
    Scalar c = Cos.of(RealScalar.of(2));
    Scalar s = DoubleScalar.of(Math.cos(2));
    assertEquals(c, s);
  }

  @Test
  void testComplex() {
    Scalar c = Cos.of(ComplexScalar.of(2, 3.));
    // -4.1896256909688072301 - 9.1092278937553365980 I
    Scalar s = Scalars.fromString("-4.189625690968807-9.109227893755337*I");
    assertEquals(c, s);
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> Cos.of(Quantity.of(1, "deg")));
  }

  @Test
  void testStringFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Throw.class, () -> Cos.of(scalar));
  }
}
