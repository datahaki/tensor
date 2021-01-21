// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DeterminateScalarQTest extends TestCase {
  public void testSimple() {
    assertTrue(DeterminateScalarQ.of(Pi.HALF));
    assertTrue(DeterminateScalarQ.of(RationalScalar.HALF));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3*I")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3.4*I")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3.4*I[s^3]")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("8.2+3.3*I[m^2]")));
    assertTrue(DeterminateScalarQ.of(Quantity.of(Pi.VALUE, "m")));
  }

  public void testNope() {
    assertFalse(DeterminateScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("abc")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("8.2+NaN*I[m^2]")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("NaN+2*I[m*s]")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("NaN+NaN*I[m*s]")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(DoubleScalar.INDETERMINATE, "m")));
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

  public void testSome() {
    assertEquals(Scalars.fromString("NaN+2*I[m*s]").toString(), "NaN+2*I[m*s]");
    // Scalar scalar = Scalars.fromString("8.2+NaN*I[m^2]");
    // System.out.println(scalar);
    // assertEquals(Scalars.fromString("8.2+NaN*I[m^2]").toString(), "8.2+NaN*I[m^2]");
  }

  public void testRequireThrow() {
    DeterminateScalarQ.require(Pi.VALUE);
    AssertFail.of(() -> DeterminateScalarQ.require(DoubleScalar.POSITIVE_INFINITY));
  }

  public void testNullFail() {
    AssertFail.of(() -> DeterminateScalarQ.of(null));
  }
}
