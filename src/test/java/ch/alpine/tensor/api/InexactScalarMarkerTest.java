// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.FiniteQ;
import ch.alpine.tensor.lie.Quaternion;

class InexactScalarMarkerTest {
  @Test
  void testQuaternion() {
    assertTrue(FiniteQ.of(Quaternion.of(0, 0, 0, Math.PI)));
    assertFalse(FiniteQ.of(Quaternion.of(0, Double.NaN, 0, 0.0)));
    assertFalse(FiniteQ.of(Quaternion.of(0, 0, Double.POSITIVE_INFINITY, 0.0)));
  }
}
