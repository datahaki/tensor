// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;

class PadRightTest {
  @Test
  void testVectorLo() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tuo = Serialization.copy(PadRight.zeros(10));
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result = tuo.apply(vector);
    assertEquals(result.extract(0, 6), vector);
    assertEquals(result.extract(6, 10), Array.zeros(4));
  }

  @Test
  void testVectorHi() {
    TensorUnaryOperator tuo = PadRight.zeros(4);
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result = tuo.apply(vector);
    assertEquals(result, vector.extract(0, 4));
  }

  @Test
  void testMatrixRegular() {
    TensorUnaryOperator tuo = PadRight.zeros(2, 4);
    Tensor vector = Tensors.fromString("{{1, 2, 3}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 2, 3, 0}, {0, 0, 0, 0}}"));
  }

  @Test
  void testMatrixIrregular1() {
    TensorUnaryOperator tuo = PadRight.zeros(3, 4);
    Tensor vector = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 2, 3, 0}, {4, 5, 0, 0}, {0, 0, 0, 0}}"));
  }

  @Test
  void testMatrixIrregular2() {
    TensorUnaryOperator tuo = PadRight.zeros(1, 2);
    Tensor vector = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 2}}"));
  }

  @Test
  void testMatrixIrregular3() {
    TensorUnaryOperator tuo = PadRight.zeros(2, 2);
    Tensor vector = Tensors.fromString("{{1}, {2}, {4, 5}}");
    Tensor result = tuo.apply(vector);
    assertEquals(result, Tensors.fromString("{{1, 0}, {2, 0}}"));
  }

  @Test
  void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(PadRight.zeros());
  }

  @Test
  void testQuantity() {
    Scalar element = Quantity.of(2, "Apples");
    TensorUnaryOperator tuo = PadRight.with(element, 3);
    Tensor tensor = tuo.apply(Tensors.fromString("{1[A], 2[V]}"));
    assertEquals(tensor.toString(), "{1[A], 2[V], 2[Apples]}");
  }

  @Test
  void testFail() {
    TensorUnaryOperator tuo = PadRight.zeros(2, 2, 6);
    assertThrows(TensorRuntimeException.class, () -> tuo.apply(Tensors.fromString("{{1}, {2}, {4, 5}}")));
  }

  @Test
  void testFail2() {
    assertThrows(IllegalArgumentException.class, () -> PadRight.zeros(-2));
    assertThrows(IllegalArgumentException.class, () -> PadRight.zeros(1, -2));
  }
}
