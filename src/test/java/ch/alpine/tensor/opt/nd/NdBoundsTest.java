// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NdBoundsTest extends TestCase {
  public void testProject() {
    NdBounds ndBounds = new NdBounds(Tensors.vector(2, 3), Tensors.vector(12, 23));
    assertEquals(ndBounds.clip(Tensors.vector(0, 0)), Tensors.vector(2, 3));
    assertEquals(ndBounds.clip(Tensors.vector(0, 20)), Tensors.vector(2, 20));
    assertEquals(ndBounds.clip(Tensors.vector(0, 40)), Tensors.vector(2, 23));
    assertEquals(ndBounds.clip(Tensors.vector(3, 40)), Tensors.vector(3, 23));
    assertEquals(ndBounds.clip(Tensors.vector(4, 10)), Tensors.vector(4, 10));
    assertEquals(ndBounds.clip(Tensors.vector(14, 10)), Tensors.vector(12, 10));
  }

  public void testSimple() {
    NdBounds ndBounds = new NdBounds(Tensors.vector(2, 3), Tensors.vector(12, 23));
    assertEquals(ndBounds.lBounds(), Tensors.vector(2, 3));
    assertEquals(ndBounds.uBounds(), Tensors.vector(12, 23));
  }
}
