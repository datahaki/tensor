// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;

public class DivideTest {
  @Test
  public void testSimple() {
    assertTrue(Divide.nonZero(3, 0).isEmpty());
    assertEquals(Divide.nonZero(3, 5).get(), RationalScalar.of(3, 5));
  }
}
