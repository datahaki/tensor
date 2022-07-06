// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;

class QuantityFailTest {
  @Test
  void testStringScalarFail() {
    Unit unit = Unit.of("a");
    assertThrows(Throw.class, () -> Quantity.of(StringScalar.of("123"), unit));
  }

  @Test
  void testScalarUnit01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Scalar) null, Unit.of("s")));
  }

  @Test
  void testScalarUnit10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(Pi.VALUE, (Unit) null));
  }

  @Test
  void testScalarString01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Scalar) null, "s"));
  }

  @Test
  void testScalarString10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(RealScalar.ONE, (String) null));
  }

  @Test
  void testNumberUnit01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Number) null, Unit.of("s")));
  }

  @Test
  void testNumberUnit10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(123, (Unit) null));
  }

  @Test
  void testNumberString01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Number) null, "s"));
  }

  @Test
  void testNumberString10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(123, (String) null));
  }
}
