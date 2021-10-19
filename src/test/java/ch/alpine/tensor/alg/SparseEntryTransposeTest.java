// code by jph
package ch.alpine.tensor.alg;

import junit.framework.TestCase;

public class SparseEntryTransposeTest extends TestCase {
  public void testVisibility() {
    assertEquals(SparseEntryTranspose.class.getModifiers() & 1, 0);
  }
}
