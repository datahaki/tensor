// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class LruCacheTest {
  @Test
  void testLru1() {
    Map<Integer, String> map = new LruCache<>(2);
    map.put(3, "1");
    map.put(4, "2");
    assertEquals(map.get(4), "2");
    map.putIfAbsent(4, "5");
    assertEquals(map.get(4), "2");
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
  void testMax() {
    Map<Integer, String> map = new LruCache<>(3);
    IntStream.range(0, 100).forEach(i -> map.put(i, "" + i));
    assertEquals(map.size(), 3);
  }

  @Test
  void testLruAccessOrder() {
    Map<Integer, String> map = new LruCache<>(2);
    map.put(3, "3");
    map.put(4, "4");
    map.put(5, "5");
    map.keySet().containsAll(Arrays.asList(4, 5));
    assertFalse(map.containsKey(3));
    Map<Integer, String> map2 = new LruCache<>(1);
    map2.putAll(map);
    assertFalse(map2.containsKey(4));
  }

  @Test
  void testLruAccessOrder2() {
    Map<Integer, String> map = new LruCache<>(2);
    map.put(3, "3");
    map.put(4, "4");
    map.get(3);
    map.put(5, "5");
    map.keySet().containsAll(Arrays.asList(3, 5));
    assertFalse(map.containsKey(4));
  }

  @Test
  void testLruAccessOrder3() {
    Map<Integer, String> map = new LruCache<>(3);
    map.put(3, "3");
    map.compute(4, (k, v) -> "" + k);
    map.computeIfPresent(3, (k, v) -> v.repeat(k));
    assertEquals(map.get(3), "333");
    map.computeIfAbsent(3, (k) -> "abc".repeat(k));
    assertEquals(map.get(3), "333");
    map.computeIfAbsent(2, k -> "ab".repeat(k));
    assertEquals(map.get(2), "abab");
  }

  @Test
  void testThreadSafe() {
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
  void testNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> new LruCache<>(-1));
    assertThrows(IllegalArgumentException.class, () -> new LruCache<>(10, -0.2f));
    assertThrows(IllegalArgumentException.class, () -> new LruCache<>(10, 0f));
    assertThrows(ArithmeticException.class, () -> new LruCache<>(Integer.MAX_VALUE));
  }

  @Test
  void testNullKey() {
    LruCache<String, Integer> lruCache = new LruCache<>(10);
    assertThrows(Exception.class, () -> lruCache.get(null));
    assertThrows(Exception.class, () -> lruCache.put(null, 3));
    HashMap<String, Integer> map = new HashMap<>();
    map.put(null, 5);
    assertThrows(Exception.class, () -> lruCache.putAll(map));
    assertThrows(Exception.class, () -> lruCache.putIfAbsent(null, 1));
    assertThrows(Exception.class, () -> lruCache.compute(null, (k, v) -> 20));
    assertThrows(Exception.class, () -> lruCache.computeIfPresent(null, (k, v) -> 30));
    assertThrows(Exception.class, () -> lruCache.computeIfAbsent(null, v -> 10));
  }
}
