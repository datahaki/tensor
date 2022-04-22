// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class LruCacheTest {
  @Test
  public void testLru1() {
    Map<Integer, String> map = new LruCache<>(2);
    map.put(3, "1");
    map.put(4, "2");
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 3);
    }
    map.get(3);
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 4);
    }
    String m1 = map.toString();
    assertTrue(map.containsKey(4)); // does not change order
    assertEquals(m1, map.toString());
    assertTrue(map.containsKey(3)); // does not change order
    assertEquals(m1, map.toString());
    map.get(3);
    assertEquals(m1, map.toString());
    map.put(4, "0");
    {
      int v = map.keySet().iterator().next();
      assertEquals(v, 3);
    }
  }

  @Test
  public void testMax() {
    Map<Integer, String> map = new LruCache<>(3);
    IntStream.range(0, 100).forEach(i -> map.put(i, "" + i));
    assertEquals(map.size(), 3);
  }

  @Test
  public void testLruAccessOrder() {
    Map<Integer, String> map = new LruCache<>(2);
    map.put(3, "3");
    map.put(4, "4");
    map.put(5, "5");
    map.keySet().containsAll(Arrays.asList(4, 5));
    assertFalse(map.containsKey(3));
  }

  @Test
  public void testLruAccessOrder2() {
    Map<Integer, String> map = new LruCache<>(2);
    map.put(3, "3");
    map.put(4, "4");
    map.get(3);
    map.put(5, "5");
    map.keySet().containsAll(Arrays.asList(3, 5));
    assertFalse(map.containsKey(4));
  }

  @Test
  public void testThreadSafe() {
    Map<Integer, Integer> map = Collections.synchronizedMap(new LruCache<>(12));
    IntStream.range(0, 1024).boxed().parallel().forEach(index -> map.put(index % 32, index));
    assertEquals(map.size(), 12);
  }
  // ci does not support this test
  // public void testThreadUnsafe() {
  // Map<Integer, Integer> map = new LruCache<>(3);
  // IntStream.range(0, 1024).boxed().parallel().forEach(index -> map.put(index, index));
  // assertTrue(3 < map.size());
  // }

  @Test
  public void testNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> new LruCache<>(-1));
    assertThrows(IllegalArgumentException.class, () -> new LruCache<>(10, -0.2f));
    assertThrows(IllegalArgumentException.class, () -> new LruCache<>(10, 0f));
    assertThrows(ArithmeticException.class, () -> new LruCache<>(Integer.MAX_VALUE));
  }
}
