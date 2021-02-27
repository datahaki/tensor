// code by jph
package ch.ethz.idsc.tensor.qty;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuantityImplTest extends TestCase {
  public void testSerializable() throws Exception {
    Quantity quantity = (Quantity) Scalars.fromString("-7+3*I[kg^-2*m*s]");
    Quantity copy = Serialization.copy(quantity);
    assertEquals(quantity, copy);
  }

  public void testSign() {
    Scalar value = ComplexScalar.of(1, 2);
    Scalar result = Sign.FUNCTION.apply(Quantity.of(value, "m*s^-2"));
    Tolerance.CHOP.requireClose(Sign.FUNCTION.apply(value), result);
  }

  public void testOneGuarantee() {
    Scalar scalar = Quantity.of(123, "m*s^-2*K^1/2");
    assertEquals(scalar.multiply(scalar.one()), scalar);
    assertEquals(scalar.one().multiply(scalar), scalar);
  }

  public void testExactIntFail() {
    Scalar scalar = Quantity.of(10, "m");
    AssertFail.of(() -> Scalars.intValueExact(scalar));
  }

  public void testNumberFail() {
    Scalar scalar = Quantity.of(11, "m*s");
    AssertFail.of(() -> scalar.number());
  }

  public void testEquals() {
    assertFalse(Quantity.of(10, "m").equals("s"));
    assertFalse(Quantity.of(10, "m").equals(Quantity.of(2, "m")));
    assertFalse(Quantity.of(10, "m").equals(Quantity.of(10, "kg")));
  }

  public void testEqualsZero() {
    assertFalse(Quantity.of(0, "m").equals(RealScalar.ZERO));
  }

  public void testHashCode() {
    assertEquals( //
        Quantity.of(10.2, "m^-1*kg").hashCode(), //
        Quantity.of(10.2, "kg*m^-1").hashCode());
  }

  public void testEmpty() {
    Scalar q1 = Quantity.of(3, "m*s");
    Scalar q2 = Quantity.of(7, "s*m");
    Scalar s3 = q1.divide(q2);
    assertTrue(s3 instanceof RationalScalar);
    assertTrue(q1.under(q2) instanceof RationalScalar);
  }

  public void testPowerQuantityQuantityFail() {
    Scalar scalar = Scalars.fromString("-7+3*I[kg^-2*m*s]");
    AssertFail.of(() -> Power.of(scalar, Quantity.of(3, "s")));
  }

  public void testPowerRealQuantityFail() {
    AssertFail.of(() -> Power.of(RealScalar.ONE, Quantity.of(3, "s")));
  }

  public void testPowerDoubleQuantityFail() {
    AssertFail.of(() -> Power.of(Pi.VALUE, Quantity.of(3, "s")));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QuantityImpl.class.getModifiers()));
  }
}
