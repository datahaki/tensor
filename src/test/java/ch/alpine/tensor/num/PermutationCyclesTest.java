// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PermutationCyclesTest {
  @Test
  void testSimple() {
    Cycles cycles = PermutationCycles.of(1, 4, 2, 5, 0, 7, 6, 8, 3, 9);
    assertEquals(cycles, TestHelper.of("{{0, 1, 4}, {3, 5, 7, 8}}"));
  }

  @Test
  void testSigmaFail() {
    assertThrows(IllegalArgumentException.class, () -> PermutationCycles.of(1, 4));
    assertThrows(IllegalArgumentException.class, () -> PermutationCycles.of(0, 0));
    assertThrows(IllegalArgumentException.class, () -> PermutationCycles.of(1, 1));
  }
}
