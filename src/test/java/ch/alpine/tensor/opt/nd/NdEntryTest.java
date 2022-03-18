// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

public class NdEntryTest {
  @Test
  public void testSimple() {
    NdEntry<Integer> ndEntry = new NdEntry<>(Tensors.vector(1, 2, 3), 123);
    String string = ndEntry.toString();
    assertEquals(string, "NdEntry[{1, 2, 3}, 123]");
  }
}
