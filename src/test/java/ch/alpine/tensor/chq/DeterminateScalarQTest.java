// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.qty.Quantity;

class DeterminateScalarQTest {
  @Test
  void testReal() {
    assertFalse(DeterminateScalarQ.of(DoubleScalar.INDETERMINATE));
    assertTrue(DeterminateScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertTrue(DeterminateScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
  }

  @Test
  void testComplex() {
    assertTrue(DeterminateScalarQ.of(ComplexScalar.of(3, Double.POSITIVE_INFINITY)));
    assertFalse(DeterminateScalarQ.of(ComplexScalar.of(3, Double.NaN)));
  }

  @Test
  void testQuantity() {
    assertTrue(DeterminateScalarQ.of(Quantity.of(Double.POSITIVE_INFINITY, "m")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(Double.NaN, "m")));
    assertTrue(DeterminateScalarQ.of(Quantity.of(ComplexScalar.of(3, Double.POSITIVE_INFINITY), "m")));
    assertFalse(DeterminateScalarQ.of(Quantity.of(ComplexScalar.of(3, Double.NaN), "m")));
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> DeterminateScalarQ.of(null));
  }
}
