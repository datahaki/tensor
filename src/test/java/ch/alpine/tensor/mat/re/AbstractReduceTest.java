// code by jph
package ch.alpine.tensor.mat.re;

import junit.framework.TestCase;

public class AbstractReduceTest extends TestCase {
  public void testVisibility() {
    assertEquals(AbstractReduce.class.getModifiers() & 1, 0);
  }
}
