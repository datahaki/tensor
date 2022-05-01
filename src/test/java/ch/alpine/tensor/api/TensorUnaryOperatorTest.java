// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class TensorUnaryOperatorTest {
  @Test
  public void testFunctionalInterface() {
    assertNotNull(TensorUnaryOperator.class.getAnnotation(FunctionalInterface.class));
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = t -> t;
    TensorUnaryOperator copy = Serialization.copy(tensorUnaryOperator);
    assertEquals(copy.apply(Tensors.vector(1, 2, 3)), Tensors.vector(1, 2, 3));
  }
}
