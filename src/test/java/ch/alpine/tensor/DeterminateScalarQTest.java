// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

public class DeterminateScalarQTest {
  @Test
  public void testTrue() {
    assertTrue(DeterminateScalarQ.of(Pi.HALF));
    assertTrue(DeterminateScalarQ.of(RationalScalar.HALF));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3*I")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3.4*I")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3.4*I[s^3]")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("8.2+3.3*I[m^2]")));
    assertTrue(DeterminateScalarQ.of(Quantity.of(Pi.VALUE, "kg")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("abc")));
  }

  @Test
  public void testFalse() {
    assertFalse(DeterminateScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("8.2+NaN*I[m^2]")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("NaN+2*I[m*s]")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("NaN+NaN*I[m*s]")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(DoubleScalar.POSITIVE_INFINITY, "s")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(DoubleScalar.NEGATIVE_INFINITY, "N")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(DoubleScalar.INDETERMINATE, "m")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(ComplexScalar.of(3, Double.NaN), "m")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(ComplexScalar.of(Double.NaN, 3), "m")));
  }

  @Test
  public void testDecimalScalar() {
    Scalar scalar = DecimalScalar.of(BigDecimal.ONE);
    assertTrue(DeterminateScalarQ.of(scalar));
  }

  @Test
  public void testComplexBranching() {
    Scalar scalar = ComplexScalar.of(Double.NaN, Double.NaN);
    assertTrue(scalar instanceof ComplexScalar);
    assertFalse(DeterminateScalarQ.of(scalar));
    assertTrue(DeterminateScalarQ.of(ComplexScalar.of(1, 2)));
    assertFalse(DeterminateScalarQ.of(ComplexScalar.of(3, Double.NaN)));
    assertFalse(DeterminateScalarQ.of(ComplexScalar.of(Double.NaN, 4)));
    assertFalse(DeterminateScalarQ.of(ComplexScalar.of(Double.NaN, Double.NaN)));
  }

  @Test
  public void testInvariance() {
    Scalar scalar = Scalars.fromString("NaN+2*I[m*s]");
    assertEquals(scalar.toString(), "NaN+2*I[m*s]");
    assertTrue(scalar instanceof Quantity);
    assertThrows(TensorRuntimeException.class, () -> DeterminateScalarQ.require(scalar));
  }

  @Test
  public void testRequireThrow() {
    DeterminateScalarQ.require(Pi.VALUE);
    assertThrows(TensorRuntimeException.class, () -> DeterminateScalarQ.require(DoubleScalar.POSITIVE_INFINITY));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> DeterminateScalarQ.of(null));
  }
}
