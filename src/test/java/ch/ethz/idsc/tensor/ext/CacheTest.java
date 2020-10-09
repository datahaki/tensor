// code by jph
package ch.ethz.idsc.tensor.ext;

import java.util.function.Function;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CacheTest extends TestCase {
  public void testSimple() {
    Function<Object, Double> function = Cache.of(k -> Math.random(), 3);
    double double1 = function.apply("eth");
    double double2 = function.apply("eth");
    assertEquals(double1, double2);
  }

  public void testInception() {
    Function<Object, Double> memo1 = Cache.of(k -> Math.random(), 32);
    AssertFail.of(() -> Cache.of(memo1, 32));
  }

  public void testMap() {
    Function<String, Integer> cache = Cache.of(k -> 1, 768);
    IntStream.range(0, 26).parallel().forEach(c1 -> {
      char chr1 = (char) (65 + c1);
      for (int c2 = 0; c2 < 26; ++c2) {
        char chr2 = (char) (65 + c2);
        for (int c3 = 0; c3 < 13; ++c3) {
          char chr3 = (char) (65 + c3);
          cache.apply(chr1 + "" + chr2 + "" + chr3);
        }
      }
    });
    int map_size = ((Cache<String, Integer>) cache).size();
    assertTrue(map_size == 768);
  }

  public void testFailNull() {
    AssertFail.of(() -> Cache.of(null, 32));
  }
}
