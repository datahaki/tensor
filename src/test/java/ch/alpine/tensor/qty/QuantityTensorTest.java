// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

class QuantityTensorTest {
  @Test
  public void testScalar() {
    Tensor tensor = QuantityTensor.of(RealScalar.ONE, Unit.of("N"));
    assertEquals(tensor, Quantity.of(1, "N"));
  }

  @Test
  public void testVectorString() {
    Tensor tensor = QuantityTensor.of(Tensors.vector(7, 2, 3), "N");
    assertEquals(tensor.Get(0), Quantity.of(7, "N"));
    assertEquals(tensor.Get(1), Quantity.of(2, "N"));
    assertEquals(tensor.Get(2), Quantity.of(3, "N"));
  }

  @Test
  public void testVector() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor nuvec = QuantityTensor.of(vector, Unit.of("m*kg^2"));
    assertEquals(nuvec, //
        Tensors.fromString("{1[kg^2*m], 2[kg^2*m], 3[kg^2*m]}"));
  }

  @Test
  public void testExample() {
    Tensor vector = Tensors.vector(2, 3, -1);
    Tensor nuvec = QuantityTensor.of(vector, Unit.of("m*s^-1"));
    assertEquals(nuvec, //
        Tensors.fromString("{2[m*s^-1], 3.0[m * s^+1 * s^-2], -1.0[s^-1 * m]}"));
  }

  @Test
  public void testFail() {
    Scalar q = Quantity.of(1, "s");
    assertThrows(TensorRuntimeException.class, () -> QuantityTensor.of(q, Unit.of("m*kg^2")));
    assertThrows(TensorRuntimeException.class, () -> QuantityTensor.of(Tensors.of(q, q), Unit.of("m*kg^2")));
  }
}
