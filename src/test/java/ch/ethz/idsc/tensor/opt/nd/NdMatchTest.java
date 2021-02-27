// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class NdMatchTest extends TestCase {
  public void testSimple() {
    NdMatch<String> ndMatch = new NdMatch<>(Tensors.vector(1, 2, 3), "abc", RealScalar.TWO);
    assertEquals(ndMatch.location(), Tensors.vector(1, 2, 3));
    assertEquals(ndMatch.value(), "abc");
    assertEquals(ndMatch.distance(), RealScalar.TWO);
  }
}
