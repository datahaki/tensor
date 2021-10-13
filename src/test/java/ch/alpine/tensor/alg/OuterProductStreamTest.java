// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;

import junit.framework.TestCase;

public class OuterProductStreamTest extends TestCase {
  public void testSimple() {
    long count = OuterProductStream.of(Size.of(Arrays.asList(3, 4)), new int[] { 1, 0 }).count();
    assertEquals(count, 12);
  }

  public void testVisibility() {
    assertEquals(OuterProductStream.class.getModifiers(), 0);
  }
}
