// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class Matrix1NormTest extends TestCase {
  public void testOneInfNorm2() {
    Tensor a = Tensors.vector(1, 2);
    Tensor b = Tensors.vector(3, 4);
    Tensor c = Tensors.of(a, b);
    assertEquals(Matrix1Norm.of(c), Scalars.fromString("6"));
    assertEquals(MatrixInfinityNorm.of(c), Scalars.fromString("7"));
  }
}
