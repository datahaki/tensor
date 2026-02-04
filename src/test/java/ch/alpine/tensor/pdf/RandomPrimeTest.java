// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class RandomPrimeTest {
  @Test
  void testSimple() {
    Distribution distribution = RandomPrime.of(3000, 4000);
    assertTrue(distribution.toString().startsWith("RandomPrime["));
    Scalar scalar = RandomVariate.of(distribution);
    Clip clip = Clips.interval(3000, 4000);
    clip.requireInside(scalar);
  }

  @Test
  void testInstantHit() {
    Distribution distribution = RandomPrime.of(13, 13);
    assertTrue(distribution.toString().startsWith("RandomPrime["));
    assertEquals(RandomVariate.of(distribution), RealScalar.of(13));
  }

  @Test
  void testFail() {
    Distribution distribution = RandomPrime.of(3000, 3000);
    assertTrue(distribution.toString().startsWith("RandomPrime["));
    assertThrows(Exception.class, () -> RandomVariate.of(distribution));
  }
}
