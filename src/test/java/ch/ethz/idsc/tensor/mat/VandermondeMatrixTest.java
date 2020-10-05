// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class VandermondeMatrixTest extends TestCase {
  public void testSimple() {
    Tensor tensor = VandermondeMatrix.of(Tensors.vector(2, 1, 3, 4));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{{1, 1, 1, 1}, {2, 1, 3, 4}, {4, 1, 9, 16}, {8, 1, 27, 64}}"));
  }

  public void test1() {
    Tensor tensor = VandermondeMatrix.of(Tensors.vector(2));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, IdentityMatrix.of(1));
  }

  public void test2() {
    Tensor tensor = VandermondeMatrix.of(Tensors.vector(2, 5));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{{1, 1}, {2, 5}}"));
  }

  public void testScalarFail() {
    AssertFail.of(() -> VandermondeMatrix.of(RealScalar.ONE));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> VandermondeMatrix.of(HilbertMatrix.of(3)));
  }
}
