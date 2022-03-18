// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;

public class ScalarArrayTest {
  @Test
  public void testEmpty() {
    Scalar[] array = ScalarArray.ofVector(Tensors.empty());
    Tensor tensor = Tensors.of(array);
    assertEquals(Tensors.empty(), tensor);
    ExactTensorQ.require(tensor);
  }

  @Test
  public void testMatrix() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}");
    Scalar[][] array = ScalarArray.ofMatrix(tensor);
    Tensor matrix = Tensors.matrix(array);
    assertEquals(tensor, matrix);
    ExactTensorQ.require(matrix);
  }

  @Test
  public void testScalarFail() {
    AssertFail.of(() -> ScalarArray.ofVector(Pi.HALF));
  }

  @Test
  public void testVectorFail() {
    AssertFail.of(() -> ScalarArray.ofVector(HilbertMatrix.of(3)));
  }

  @Test
  public void testMatrixFail() {
    AssertFail.of(() -> ScalarArray.ofMatrix(Array.zeros(2, 2, 2)));
  }
}
