// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NdEntryTest extends TestCase {
  public void testSimple() {
    NdEntry<Integer> ndEntry = new NdEntry<>(Tensors.vector(1, 2, 3), 123);
    String string = ndEntry.toString();
    assertEquals(string, "NdEntry[{1, 2, 3}, 123]");
  }
}
