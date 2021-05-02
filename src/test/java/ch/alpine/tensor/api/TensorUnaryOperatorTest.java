// code by jph
package ch.alpine.tensor.api;

import java.io.IOException;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class TensorUnaryOperatorTest extends TestCase {
  public void testFunctionalInterface() {
    assertNotNull(TensorUnaryOperator.class.getAnnotation(FunctionalInterface.class));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = t -> t;
    TensorUnaryOperator copy = Serialization.copy(tensorUnaryOperator);
    assertEquals(copy.apply(Tensors.vector(1, 2, 3)), Tensors.vector(1, 2, 3));
  }
}
