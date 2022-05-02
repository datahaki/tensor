// code by jph
package ch.alpine.tensor.usr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.ArgMax;
import ch.alpine.tensor.ext.ArgMin;

/** the location of the test here asserts that the constants are public:
 * ArgMin.EMPTY
 * ArgMax.EMPTY */
class ArgEmptyTest {
  @Test
  public void testConvention() {
    assertEquals(ArgMin.EMPTY, -1);
    assertEquals(ArgMax.EMPTY, -1);
  }
}
