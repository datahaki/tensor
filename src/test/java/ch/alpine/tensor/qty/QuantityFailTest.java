// code by jph
package ch.alpine.tensor.qty;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;

public class QuantityFailTest {
  @Test
  public void testStringScalarFail() {
    Unit unit = Unit.of("a");
    AssertFail.of(() -> Quantity.of(StringScalar.of("123"), unit));
  }

  @Test
  public void testScalarUnit01Fail() {
    AssertFail.of(() -> Quantity.of((Scalar) null, Unit.of("s")));
  }

  @Test
  public void testScalarUnit10Fail() {
    AssertFail.of(() -> Quantity.of(Pi.VALUE, (Unit) null));
  }

  @Test
  public void testScalarString01Fail() {
    AssertFail.of(() -> Quantity.of((Scalar) null, "s"));
  }

  @Test
  public void testScalarString10Fail() {
    AssertFail.of(() -> Quantity.of(RealScalar.ONE, (String) null));
  }

  @Test
  public void testNumberUnit01Fail() {
    AssertFail.of(() -> Quantity.of((Number) null, Unit.of("s")));
  }

  @Test
  public void testNumberUnit10Fail() {
    AssertFail.of(() -> Quantity.of(123, (Unit) null));
  }

  @Test
  public void testNumberString01Fail() {
    AssertFail.of(() -> Quantity.of((Number) null, "s"));
  }

  @Test
  public void testNumberString10Fail() {
    AssertFail.of(() -> Quantity.of(123, (String) null));
  }
}
