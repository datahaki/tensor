// code by jph
package ch.alpine.tensor.num;

import java.util.Random;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RandomPrimeTest extends TestCase {
  public void testSimple() {
    Random random = new Random();
    RandomPrime.of(10, random);
  }

  public void testFail() {
    Random random = new Random();
    RandomPrime.of(Prime.MAX_INDEX, random);
    AssertFail.of(() -> RandomPrime.of(Prime.MAX_INDEX + 1, random));
  }
}
