// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class MatrixInfinityNormTest extends TestCase {
  public void testOneInfNorm3() {
    Tensor a = Tensors.vector(1, 2, 8);
    Tensor b = Tensors.vector(3, 4, 2);
    Tensor c = Tensors.of(a, b);
    assertEquals(Matrix1Norm.of(c), Scalars.fromString("10"));
    assertEquals(MatrixInfinityNorm.of(c), Scalars.fromString("11"));
  }
}
