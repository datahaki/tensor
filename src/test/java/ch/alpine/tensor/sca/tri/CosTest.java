// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;

class CosTest {
  @Test
  void testReal() {
    Scalar c = Cos.FUNCTION.apply(RealScalar.of(2));
    Scalar s = DoubleScalar.of(Math.cos(2));
    assertEquals(c, s);
  }

  @Test
  void testComplex() {
    Scalar c = Cos.FUNCTION.apply(ComplexScalar.of(2, 3.));
    Scalar s = ComplexScalar.of(-4.189625690968807, -9.109227893755337);
    assertEquals(c, s);
  }

  @Test
  void testCos() {
    String mathematica = "-0.416146836547142386997568229500762189766000771075544890755149973";
    Scalar x = DecimalScalar.of(BigDecimal.valueOf(2));
    Scalar s0 = Cos.FUNCTION.apply(x);
    assertTrue(Objects.toString(s0).startsWith(mathematica.substring(0, 30)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Exception.class, () -> Cos.FUNCTION.apply(Quantity.of(1, "deg")));
  }

  @Test
  void testStringFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Exception.class, () -> Cos.FUNCTION.apply(scalar));
  }
}
