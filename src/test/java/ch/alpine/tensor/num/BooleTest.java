// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

public class BooleTest {
  @Test
  public void testTrue() {
    assertEquals(Boole.of(true), RealScalar.ONE);
  }

  @Test
  public void testFalse() {
    assertEquals(Boole.of(false), RealScalar.ZERO);
  }
}
