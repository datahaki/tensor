// code by jph
package ch.alpine.tensor.num;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class RandomPrimeTest {
  @Test
  public void testSimple() {
    Random random = new Random();
    RandomPrime.of(10, random);
  }

  @Test
  public void testFail() {
    Random random = new Random();
    RandomPrime.of(Prime.MAX_INDEX, random);
    AssertFail.of(() -> RandomPrime.of(Prime.MAX_INDEX + 1, random));
  }
}
