// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class StaticHelperTest {
  @Test
  public void testSimple() {
    assertTrue(StaticHelper.isFinished(RealScalar.ZERO, RealScalar.ONE));
  }
}
