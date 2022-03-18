// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class PermutationCyclesTest {
  @Test
  public void testSimple() {
    Cycles cycles = PermutationCycles.of(1, 4, 2, 5, 0, 7, 6, 8, 3, 9);
    assertEquals(cycles, Cycles.of("{{0, 1, 4}, {3, 5, 7, 8}}"));
  }

  @Test
  public void testSigmaFail() {
    AssertFail.of(() -> PermutationCycles.of(1, 4));
    AssertFail.of(() -> PermutationCycles.of(0, 0));
    AssertFail.of(() -> PermutationCycles.of(1, 1));
  }
}
