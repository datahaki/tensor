// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NdMatchTest extends TestCase {
  public void testSimple() {
    NdMatch<String> ndMatch = new NdMatch<>(Tensors.vector(1, 2, 3), "abc", RealScalar.TWO);
    assertEquals(ndMatch.location(), Tensors.vector(1, 2, 3));
    assertEquals(ndMatch.value(), "abc");
    assertEquals(ndMatch.distance(), RealScalar.TWO);
  }
}
