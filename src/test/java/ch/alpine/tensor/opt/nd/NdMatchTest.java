// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

public class NdMatchTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    NdMatch<String> ndMatch = Serialization.copy( //
        new NdMatch<>(new NdEntry<>(Tensors.vector(1, 2, 3), "abc"), RealScalar.TWO));
    assertEquals(ndMatch.location(), Tensors.vector(1, 2, 3));
    assertEquals(ndMatch.value(), "abc");
    assertEquals(ndMatch.distance(), RealScalar.TWO);
    assertEquals(ndMatch.toString(), "NdMatch[NdEntry[{1, 2, 3}, abc], 2]");
  }
}
