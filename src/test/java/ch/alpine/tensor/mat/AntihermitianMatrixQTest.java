// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import junit.framework.TestCase;

public class AntihermitianMatrixQTest extends TestCase {
  public void testSimple() {
    assertTrue(AntihermitianMatrixQ.of(Array.zeros(2, 2)));
    assertFalse(AntihermitianMatrixQ.of(HilbertMatrix.of(3)));
  }

  public void test2x2() {
    Tensor matrix = Tensors.fromString("{{0,1+2*I},{-1+2*I,0}}");
    AntihermitianMatrixQ.require(matrix);
  }
}
