// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ext.ArgMax;
import ch.alpine.tensor.ext.ArgMin;
import junit.framework.TestCase;

public class ArgEmptyTest extends TestCase {
  public void testConvention() {
    assertEquals(ArgMin.EMPTY, -1);
    assertEquals(ArgMax.EMPTY, -1);
  }
}
