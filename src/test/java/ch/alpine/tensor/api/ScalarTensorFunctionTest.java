// code by jph
package ch.alpine.tensor.api;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class ScalarTensorFunctionTest extends TestCase {
  public void testFunctionalInterface() {
    assertNotNull(ScalarTensorFunction.class.getAnnotation(FunctionalInterface.class));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    ScalarTensorFunction tensorScalarFunction = s -> Tensors.of(s, s, s);
    ScalarTensorFunction copy = Serialization.copy(tensorScalarFunction);
    assertEquals(copy.apply(RealScalar.ONE), Tensors.vector(1, 1, 1));
  }
}
