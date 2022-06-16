// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class ScalarTensorFunctionTest {
  @Test
  void testFunctionalInterface() {
    assertNotNull(ScalarTensorFunction.class.getAnnotation(FunctionalInterface.class));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    ScalarTensorFunction tensorScalarFunction = s -> Tensors.of(s, s, s);
    ScalarTensorFunction copy = Serialization.copy(tensorScalarFunction);
    assertEquals(copy.apply(RealScalar.ONE), Tensors.vector(1, 1, 1));
  }
}
