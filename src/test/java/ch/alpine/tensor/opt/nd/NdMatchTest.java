// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class NdMatchTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    NdMatch<String> ndMatch = Serialization.copy( //
        new NdMatch<>(new NdEntry<>(Tensors.vector(1, 2, 3), "abc"), RealScalar.TWO));
    assertEquals(ndMatch.location(), Tensors.vector(1, 2, 3));
    assertEquals(ndMatch.value(), "abc");
    assertEquals(ndMatch.distance(), RealScalar.TWO);
    assertEquals(ndMatch.toString(), "NdMatch[NdEntry[{1, 2, 3}, abc], 2]");
  }
}
