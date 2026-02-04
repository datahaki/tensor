// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.lie.rot.Quaternion;

class InexactScalarMarkerTest {
  @Test
  void testQuaternion() {
    assertTrue(FiniteScalarQ.of(Quaternion.of(0, 0, 0, Math.PI)));
    assertFalse(FiniteScalarQ.of(Quaternion.of(0, Double.NaN, 0, 0.0)));
    assertFalse(FiniteScalarQ.of(Quaternion.of(0, 0, Double.POSITIVE_INFINITY, 0.0)));
  }
}
