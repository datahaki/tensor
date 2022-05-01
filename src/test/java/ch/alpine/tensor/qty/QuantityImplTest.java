// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.tri.ArcTan;

public class QuantityImplTest {
  @Test
  public void testSerializable() throws Exception {
    Quantity quantity = (Quantity) Scalars.fromString("-7+3*I[kg^-2*m*s]");
    Quantity copy = Serialization.copy(quantity);
    assertEquals(quantity, copy);
  }

  @Test
  public void testSign() {
    Scalar value = ComplexScalar.of(1, 2);
    Scalar result = Sign.FUNCTION.apply(Quantity.of(value, "m*s^-2"));
    Tolerance.CHOP.requireClose(Sign.FUNCTION.apply(value), result);
  }

  @Test
  public void testOneGuarantee() {
    Scalar scalar = Quantity.of(123, "m*s^-2*K^1/2");
    assertEquals(scalar.multiply(scalar.one()), scalar);
    assertEquals(scalar.one().multiply(scalar), scalar);
  }

  @Test
  public void testExactIntFail() {
    Scalar scalar = Quantity.of(10, "m");
    assertThrows(TensorRuntimeException.class, () -> Scalars.intValueExact(scalar));
  }

  @Test
  public void testNumberFail() {
    Scalar scalar = Quantity.of(11, "m*s");
    assertThrows(TensorRuntimeException.class, () -> scalar.number());
  }

  @Test
  public void testEquals() {
    assertFalse(Quantity.of(10, "m").equals(Quantity.of(2, "m")));
    assertFalse(Quantity.of(10, "m").equals(Quantity.of(10, "kg")));
  }

  @Test
  public void testEqualsObject() {
    Object object = Quantity.of(10, "m");
    assertFalse(object.equals("s"));
  }

  @Test
  public void testEqualsZero() {
    assertFalse(Quantity.of(0, "m").equals(RealScalar.ZERO));
  }

  @Test
  public void testArg() { // checked with Mathematica
    Scalar scalar = Quantity.of(ComplexScalar.of(2, 1), "m");
    Scalar arg = Arg.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(arg, ArcTan.FUNCTION.apply(RationalScalar.HALF));
    Tolerance.CHOP.requireClose(arg, ArcTan.of(2, 1));
  }

  @Test
  public void testHashCode() {
    assertEquals( //
        Quantity.of(10.2, "m^-1*kg").hashCode(), //
        Quantity.of(10.2, "kg*m^-1").hashCode());
  }

  @Test
  public void testEmpty() {
    Scalar q1 = Quantity.of(3, "m*s");
    Scalar q2 = Quantity.of(7, "s*m");
    Scalar s3 = q1.divide(q2);
    assertInstanceOf(RationalScalar.class, s3);
    assertInstanceOf(RationalScalar.class, q1.under(q2));
  }

  @Test
  public void testPowerQuantityQuantityFail() {
    Scalar scalar = Scalars.fromString("-7+3*I[kg^-2*m*s]");
    assertThrows(TensorRuntimeException.class, () -> Power.of(scalar, Quantity.of(3, "s")));
  }

  @Test
  public void testPowerRealQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Power.of(RealScalar.ONE, Quantity.of(3, "s")));
  }

  @Test
  public void testPowerDoubleQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Power.of(Pi.VALUE, Quantity.of(3, "s")));
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QuantityImpl.class.getModifiers()));
  }
}
