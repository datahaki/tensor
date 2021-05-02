// code by jph
package ch.alpine.tensor.api;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class TensorScalarFunctionTest extends TestCase {
  public void testFunctionalInterface() {
    assertNotNull(TensorScalarFunction.class.getAnnotation(FunctionalInterface.class));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorScalarFunction tensorScalarFunction = t -> t.Get(0);
    TensorScalarFunction copy = Serialization.copy(tensorScalarFunction);
    assertEquals(copy.apply(Tensors.vector(1, 2, 3)), RealScalar.ONE);
  }
}
