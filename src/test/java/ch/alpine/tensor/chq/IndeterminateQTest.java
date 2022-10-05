// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;

class IndeterminateQTest {
  @Test
  void test() {
    assertTrue(IndeterminateQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(IndeterminateQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(IndeterminateQ.of(DoubleScalar.NEGATIVE_INFINITY));
  }
}
