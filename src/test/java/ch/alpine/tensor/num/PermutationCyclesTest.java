// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.lie.Permutations;

class PermutationCyclesTest {
  @Test
  void testSimple() {
    Cycles cycles = PermutationCycles.of(1, 4, 2, 5, 0, 7, 6, 8, 3, 9);
    assertEquals(cycles, Cycles.of("{{0, 1, 4}, {3, 5, 7, 8}}"));
  }

  @Test
  void testSigmaFail() {
    assertThrows(IllegalArgumentException.class, () -> PermutationCycles.of(1, 4));
    assertThrows(IllegalArgumentException.class, () -> PermutationCycles.of(0, 0));
    assertThrows(IllegalArgumentException.class, () -> PermutationCycles.of(1, 1));
  }

  @RepeatedTest(7)
  void testLoop(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    for (Tensor index : Permutations.of(Range.of(0, n))) {
      int[] sigma = Primitives.toIntArray(index);
      Cycles cycles = PermutationCycles.of(sigma);
      int[] cycle = PermutationList.of(cycles, n);
      assertTrue(Arrays.equals(sigma, cycle));
    }
  }
}
