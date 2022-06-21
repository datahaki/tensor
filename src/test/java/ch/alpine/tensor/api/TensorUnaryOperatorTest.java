// code by jph, gjoel
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.fft.ListCorrelate;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Tan;

class TensorUnaryOperatorTest {
  @Test
  void testChain() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tuo = TensorUnaryOperator.chain(ListCorrelate.with(Tensors.vector(1, 2)), ListCorrelate.with(Tensors.vector(3, 4)));
    Serialization.copy(tuo);
    tuo.apply(Tensors.vector(1, 2, 3, 4, 5, 6, 7, 8));
  }

  @Test
  void testFunctionalInterface() {
    assertNotNull(TensorUnaryOperator.class.getAnnotation(FunctionalInterface.class));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = t -> t;
    TensorUnaryOperator copy = Serialization.copy(tensorUnaryOperator);
    assertEquals(copy.apply(Tensors.vector(1, 2, 3)), Tensors.vector(1, 2, 3));
  }

  @Test
  void testSerializableId() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = TensorUnaryOperator.chain();
    TensorUnaryOperator copy = Serialization.copy(tensorUnaryOperator);
    assertEquals(copy.apply(Tensors.vector(1, 2, 4)), Tensors.vector(1, 2, 4));
  }

  @Test
  void testCompose() {
    TensorUnaryOperator tuo1 = Sin::of;
    TensorUnaryOperator tuo2 = Tan::of;
    TensorUnaryOperator result = tuo1.compose(tuo2);
    Tensor s1 = result.apply(RealScalar.of(0.3));
    Scalar s2 = Sin.FUNCTION.apply(Tan.FUNCTION.apply(RealScalar.of(0.3)));
    assertEquals(s1, s2);
  }

  @Test
  void testComposeFail() {
    assertThrows(Exception.class, () -> ListCorrelate.with(Tensors.vector(1, 2)).compose(null));
    assertThrows(Exception.class, () -> ListCorrelate.with(Tensors.vector(1, 2)).andThen(null));
  }

  @Test
  void testChainEmpty() {
    TensorUnaryOperator tuo1 = TensorUnaryOperator.chain();
    TensorUnaryOperator tuo2 = TensorUnaryOperator.chain();
    assertEquals(tuo1, tuo2);
  }

  @Test
  void testChainFail() {
    assertThrows(Exception.class, () -> TensorUnaryOperator.chain(ListCorrelate.with(Tensors.vector(1, 2)), null));
  }
}
