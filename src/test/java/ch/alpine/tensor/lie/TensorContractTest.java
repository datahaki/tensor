// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.VectorQ;

public class TensorContractTest {
  @Test
  public void testRank3() {
    Tensor vector = TensorContract.of(LeviCivitaTensor.of(3), 0, 2);
    assertTrue(VectorQ.ofLength(vector, 3));
    assertEquals(vector, Array.zeros(3));
    ExactTensorQ.require(vector);
  }

  @Test
  public void testFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> TensorContract.of(LeviCivitaTensor.of(3), 0, 3));
  }

  @Test
  public void testContraction() {
    Tensor array = Array.of(Tensors::vector, 2, 3, 2, 4);
    assertThrows(IllegalArgumentException.class, () -> TensorContract.of(array, 0, 3));
    Tensor tensor = TensorContract.of(array, 0, 2);
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 4, 4));
  }
}
