// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;

public class QuantityFailTest {
  @Test
  public void testStringScalarFail() {
    Unit unit = Unit.of("a");
    assertThrows(TensorRuntimeException.class, () -> Quantity.of(StringScalar.of("123"), unit));
  }

  @Test
  public void testScalarUnit01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Scalar) null, Unit.of("s")));
  }

  @Test
  public void testScalarUnit10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(Pi.VALUE, (Unit) null));
  }

  @Test
  public void testScalarString01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Scalar) null, "s"));
  }

  @Test
  public void testScalarString10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(RealScalar.ONE, (String) null));
  }

  @Test
  public void testNumberUnit01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Number) null, Unit.of("s")));
  }

  @Test
  public void testNumberUnit10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(123, (Unit) null));
  }

  @Test
  public void testNumberString01Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of((Number) null, "s"));
  }

  @Test
  public void testNumberString10Fail() {
    assertThrows(NullPointerException.class, () -> Quantity.of(123, (String) null));
  }
}
