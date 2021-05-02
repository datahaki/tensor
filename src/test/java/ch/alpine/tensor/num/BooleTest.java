// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class BooleTest extends TestCase {
  public void testTrue() {
    assertEquals(Boole.of(true), RealScalar.ONE);
  }

  public void testFalse() {
    assertEquals(Boole.of(false), RealScalar.ZERO);
  }
}
