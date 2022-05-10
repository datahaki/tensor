// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

class RandomPrimeTest {
  @Test
  public void testSimple() {
    Random random = new Random();
    RandomPrime.of(10, random);
  }

  @Test
  public void testFail() {
    Random random = new Random();
    RandomPrime.of(Prime.MAX_INDEX, random);
    assertThrows(IllegalArgumentException.class, () -> RandomPrime.of(Prime.MAX_INDEX + 1, random));
  }
}
