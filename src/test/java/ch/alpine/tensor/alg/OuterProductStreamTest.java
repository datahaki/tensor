// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;

import junit.framework.TestCase;

public class OuterProductStreamTest extends TestCase {
  public void testSimple() {
    long count = OuterProductStream.of(Size.of(Arrays.asList(3, 4)), new int[] { 1, 0 }, true).count();
    assertEquals(count, 12);
  }

  public void testReverse() {
    long count = OuterProductStream.of(Size.of(Arrays.asList(3, 4, 2)), new int[] { 2, 1, 0 }, false).count();
    assertEquals(count, 24);
  }
}
