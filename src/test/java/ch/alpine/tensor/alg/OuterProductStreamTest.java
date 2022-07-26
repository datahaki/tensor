// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class OuterProductStreamTest {
  @Test
  void testSimple() {
    long count = OuterProductStream.of(Size.of(Arrays.asList(3, 4)), new int[] { 1, 0 }).count();
    assertEquals(count, 12);
  }

  @Test
  void testVisibility() {
    assertEquals(OuterProductStream.class.getModifiers(), 0);
  }

  @Test
  void testSpecific() {
    IntStream intStream = OuterProductStream.of(Size.of(Arrays.asList(3, 2, 4)), new int[] { 0, 2, 1 });
    intStream.forEach(System.out::println);
    // assertEquals(count, 12);
  }
}
