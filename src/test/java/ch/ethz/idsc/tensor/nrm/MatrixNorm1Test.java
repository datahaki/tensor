// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class MatrixNorm1Test extends TestCase {
  public void testOneInfNorm1() {
    Tensor a = Tensors.vector(3, -4);
    assertEquals(VectorNorm1.of(a), Scalars.fromString("7"));
    assertEquals(VectorNormInfinity.of(a), Scalars.fromString("4"));
  }

  public void testOneInfNorm2() {
    Tensor a = Tensors.vector(1, 2);
    Tensor b = Tensors.vector(3, 4);
    Tensor c = Tensors.of(a, b);
    assertEquals(MatrixNorm1.of(c), Scalars.fromString("6"));
    assertEquals(MatrixNormInfinity.of(c), Scalars.fromString("7"));
  }

  public void testOneInfNorm3() {
    Tensor a = Tensors.vector(1, 2, 8);
    Tensor b = Tensors.vector(3, 4, 2);
    Tensor c = Tensors.of(a, b);
    assertEquals(MatrixNorm1.of(c), Scalars.fromString("10"));
    assertEquals(MatrixNormInfinity.of(c), Scalars.fromString("11"));
  }
}
