// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PermutationCyclesTest extends TestCase {
  public void testSimple() {
    Cycles cycles = PermutationCycles.of(1, 4, 2, 5, 0, 7, 6, 8, 3, 9);
    assertEquals(cycles, Cycles.of("{{0, 1, 4}, {3, 5, 7, 8}}"));
  }

  public void testSigmaFail() {
    AssertFail.of(() -> PermutationCycles.of(1, 4));
    AssertFail.of(() -> PermutationCycles.of(0, 0));
    AssertFail.of(() -> PermutationCycles.of(1, 1));
  }
}
