// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RandomPermutationTest extends TestCase {
  public void testSimple() {
    for (int count = 0; count < 10; ++count)
      assertEquals(RandomPermutation.ofLength(count).length, count);
  }

  public void testFail() {
    AssertFail.of(() -> RandomPermutation.ofLength(-1));
  }
}
