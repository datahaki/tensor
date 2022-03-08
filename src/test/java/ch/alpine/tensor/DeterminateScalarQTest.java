// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DeterminateScalarQTest extends TestCase {
  public void testTrue() {
    assertTrue(DeterminateScalarQ.of(Pi.HALF));
    assertTrue(DeterminateScalarQ.of(RationalScalar.HALF));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3*I")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3.4*I")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3.4*I[s^3]")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("8.2+3.3*I[m^2]")));
    assertTrue(DeterminateScalarQ.of(Quantity.of(Pi.VALUE, "kg")));
  }

  public void testFalse() {
    assertFalse(DeterminateScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("abc")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("8.2+NaN*I[m^2]")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("NaN+2*I[m*s]")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("NaN+NaN*I[m*s]")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(DoubleScalar.POSITIVE_INFINITY, "s")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(DoubleScalar.NEGATIVE_INFINITY, "N")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(DoubleScalar.INDETERMINATE, "m")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(ComplexScalar.of(3, Double.NaN), "m")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(ComplexScalar.of(Double.NaN, 3), "m")));
  }

  public void testComplexBranching() {
    Scalar scalar = ComplexScalar.of(Double.NaN, Double.NaN);
    assertTrue(scalar instanceof ComplexScalar);
    assertFalse(DeterminateScalarQ.of(scalar));
    assertTrue(DeterminateScalarQ.of(ComplexScalar.of(1, 2)));
    assertFalse(DeterminateScalarQ.of(ComplexScalar.of(3, Double.NaN)));
    assertFalse(DeterminateScalarQ.of(ComplexScalar.of(Double.NaN, 4)));
    assertFalse(DeterminateScalarQ.of(ComplexScalar.of(Double.NaN, Double.NaN)));
  }

  public void testInvariance() {
    Scalar scalar = Scalars.fromString("NaN+2*I[m*s]");
    assertEquals(scalar.toString(), "NaN+2*I[m*s]");
    assertTrue(scalar instanceof Quantity);
    AssertFail.of(() -> DeterminateScalarQ.require(scalar));
  }

  public void testRequireThrow() {
    DeterminateScalarQ.require(Pi.VALUE);
    AssertFail.of(() -> DeterminateScalarQ.require(DoubleScalar.POSITIVE_INFINITY));
  }

  public void testNullFail() {
    AssertFail.of(() -> DeterminateScalarQ.of(null));
  }
}
