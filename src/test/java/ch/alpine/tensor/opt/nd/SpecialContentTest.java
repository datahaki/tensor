// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.IOException;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class SpecialContentTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    SpecialContent sc = new SpecialContent();
    SpecialContent cp = Serialization.copy(sc);
    assertEquals(cp.value, Tensors.vector(1, 2, 3));
    assertEquals(cp.handled, Tensors.vector(99, 100));
  }
}
