// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;

class IndeterminateQTest {
  @Test
  void testReal() {
    assertTrue(IndeterminateQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(IndeterminateQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(IndeterminateQ.of(DoubleScalar.NEGATIVE_INFINITY));
  }

  @Test
  void testComplex() {
    assertFalse(IndeterminateQ.of(ComplexScalar.of(3, Double.POSITIVE_INFINITY)));
    assertTrue(IndeterminateQ.of(ComplexScalar.of(3, Double.NaN)));
  }
}
