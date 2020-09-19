// code by jph
package ch.ethz.idsc.tensor.qty;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class QuantityFailTest extends TestCase {
  public void testStringScalarFail() {
    Unit unit = Unit.of("a");
    try {
      Quantity.of(StringScalar.of("123"), unit);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  /***************************************************/
  public void testScalarUnit01Fail() {
    try {
      Quantity.of((Scalar) null, Unit.of("s"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarUnit10Fail() {
    try {
      Quantity.of(Pi.VALUE, (Unit) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  /***************************************************/
  public void testScalarString01Fail() {
    try {
      Quantity.of((Scalar) null, "s");
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarString10Fail() {
    try {
      Quantity.of(RealScalar.ONE, (String) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  /***************************************************/
  public void testNumberUnit01Fail() {
    try {
      Quantity.of((Number) null, Unit.of("s"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNumberUnit10Fail() {
    try {
      Quantity.of(123, (Unit) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  /***************************************************/
  public void testNumberString01Fail() {
    try {
      Quantity.of((Number) null, "s");
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNumberString10Fail() {
    try {
      Quantity.of(123, (String) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
