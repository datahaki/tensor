// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
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
  void testBigDecimal() {
    String mathematica = "0.9092974268256816953960198659117448427022549714478902683789730115";
    Scalar x = DecimalScalar.of(BigDecimal.valueOf(2));
    Scalar s0 = Sin.FUNCTION.apply(x);
    assertTrue(Objects.toString(s0).startsWith(mathematica.substring(0, 30)));
  }

  @ParameterizedTest
  @ValueSource(booleans = { true, false })
  void testToggle(boolean value) {
    final boolean copy = value;
    assertEquals(!value, value ^ true);
    value ^= true;
    assertEquals(value, !copy);
    value ^= false;
    assertEquals(value, !copy);
    value ^= true;
    assertEquals(value, copy);
  }

  @Test
  void testQuantityFail() {
    assertThrows(Exception.class, () -> Sin.FUNCTION.apply(Quantity.of(1, "deg")));
  }

  @Test
  void testStringScalarFail() {
    assertThrows(Exception.class, () -> Sin.FUNCTION.apply(StringScalar.of("some")));
  }
}
