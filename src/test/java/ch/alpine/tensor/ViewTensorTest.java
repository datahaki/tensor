// code by jph
package ch.alpine.tensor;

import java.util.Arrays;

import ch.alpine.tensor.alg.Array;
import junit.framework.TestCase;

public class ViewTensorTest extends TestCase {
  public void testBlock() {
    Tensor array = Array.zeros(5, 5);
    Tensor refs = array;
    refs.block(Arrays.asList(1, 2), Arrays.asList(2, 3)).set(RealScalar.ONE::add, Tensor.ALL, Tensor.ALL);
    assertEquals(array, //
        Tensors.fromString("{{0, 0, 0, 0, 0}, {0, 0, 1, 1, 1}, {0, 0, 1, 1, 1}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}}"));
  }

  public void testExtract() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor slevel = vector.extract(1, 3);
    slevel.set(RealScalar.ONE::add, 0);
    assertEquals(vector, Tensors.vector(1, 2, 3));
  }
}
