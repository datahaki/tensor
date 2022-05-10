// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class OuterProductStreamTest {
  @Test
  public void testSimple() {
    long count = OuterProductStream.of(Size.of(Arrays.asList(3, 4)), new int[] { 1, 0 }).count();
    assertEquals(count, 12);
  }

  @Test
  public void testVisibility() {
    assertEquals(OuterProductStream.class.getModifiers(), 0);
  }
}
