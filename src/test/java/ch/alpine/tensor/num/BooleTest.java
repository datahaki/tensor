// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class BooleTest {
  @Test
  void testTrue() {
    assertEquals(Boole.of(true), RealScalar.ONE);
  }

  @Test
  void testFalse() {
    assertEquals(Boole.of(false), RealScalar.ZERO);
  }
}
