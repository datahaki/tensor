// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuantityFailTest extends TestCase {
  public void testStringScalarFail() {
    Unit unit = Unit.of("a");
    AssertFail.of(() -> Quantity.of(StringScalar.of("123"), unit));
  }

  public void testScalarUnit01Fail() {
    AssertFail.of(() -> Quantity.of((Scalar) null, Unit.of("s")));
  }

  public void testScalarUnit10Fail() {
    AssertFail.of(() -> Quantity.of(Pi.VALUE, (Unit) null));
  }

  public void testScalarString01Fail() {
    AssertFail.of(() -> Quantity.of((Scalar) null, "s"));
  }

  public void testScalarString10Fail() {
    AssertFail.of(() -> Quantity.of(RealScalar.ONE, (String) null));
  }

  public void testNumberUnit01Fail() {
    AssertFail.of(() -> Quantity.of((Number) null, Unit.of("s")));
  }

  public void testNumberUnit10Fail() {
    AssertFail.of(() -> Quantity.of(123, (Unit) null));
  }

  public void testNumberString01Fail() {
    AssertFail.of(() -> Quantity.of((Number) null, "s"));
  }

  public void testNumberString10Fail() {
    AssertFail.of(() -> Quantity.of(123, (String) null));
  }
}
