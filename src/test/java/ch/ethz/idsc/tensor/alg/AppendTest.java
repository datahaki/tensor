// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class AppendTest extends TestCase {
  public void testSimple() {
    Tensor x = Tensors.vector(1, 2, 3);
    Tensor y = Tensors.vector(4, 5, 6);
    Tensor tensor = Append.of(x, y);
    tensor.set(RealScalar.ZERO, 0);
    tensor.set(Array.zeros(3), 3);
    assertEquals(x, Tensors.vector(1, 2, 3));
    assertEquals(y, Tensors.vector(4, 5, 6));
  }

  public void testLast() {
    Tensor x = Tensors.vector(1, 2, 3);
    Tensor y = Tensors.vector(4, 5, 6);
    Tensor tensor = Append.of(x, y);
    y.set(RealScalar.ZERO, 1);
    assertEquals(tensor, Tensors.fromString("{1, 2, 3, {4, 5, 6}}"));
  }
}
