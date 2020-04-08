// code by jph
package ch.ethz.idsc.tensor.io;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class ScalarArrayTest extends TestCase {
  public void testEmpty() {
    Scalar[] array = ScalarArray.ofVector(Tensors.empty());
    Tensor tensor = Tensors.of(array);
    assertEquals(Tensors.empty(), tensor);
    ExactTensorQ.require(tensor);
  }

  public void testMatrix() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}");
    Scalar[][] array = ScalarArray.ofMatrix(tensor);
    Tensor matrix = Tensors.matrix(array);
    assertEquals(tensor, matrix);
    ExactTensorQ.require(matrix);
  }

  public void testScalarFail() {
    try {
      ScalarArray.ofVector(Pi.HALF);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testVectorFail() {
    try {
      ScalarArray.ofVector(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrixFail() {
    try {
      ScalarArray.ofMatrix(Array.zeros(2, 2, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
