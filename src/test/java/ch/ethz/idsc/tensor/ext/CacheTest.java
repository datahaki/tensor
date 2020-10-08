// code by jph
package ch.ethz.idsc.tensor.ext;

import java.util.function.Function;

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

  public void testFailNull() {
    AssertFail.of(() -> Cache.of(null, 32));
  }
}
