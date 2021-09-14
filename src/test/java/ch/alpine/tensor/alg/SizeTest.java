// code by jph
package ch.alpine.tensor.alg;

import junit.framework.TestCase;

public class SizeTest extends TestCase {
  public void testIndexOf() {
    Size size = Size.of(new int[] { 4, 2, 3 });
    assertEquals(size.indexOf(new int[] { 0, 0, 0 }, new int[] { 0, 1, 2 }), 0);
    assertEquals(size.indexOf(new int[] { 3, 1, 2 }, new int[] { 0, 1, 2 }), 23);
  }

  public void testString() {
    Size size = Size.of(new int[] { 4, 2, 3 });
    String string = size.toString();
    assertFalse(string.isEmpty());
  }
}
