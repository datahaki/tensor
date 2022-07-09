// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.VectorNorm;
import ch.alpine.tensor.sca.tri.Sin;

class TensorScalarFunctionTest {
  @Test
  void testCompose() {
    TensorScalarFunction tsf = VectorNorm.of(2).andThen(Sin.FUNCTION);
    tsf.apply(Tensors.vector(1, 2, 3, 4));
  }

  @Test
  void testFunctionalInterface() {
    assertNotNull(TensorScalarFunction.class.getAnnotation(FunctionalInterface.class));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    TensorScalarFunction tensorScalarFunction = t -> t.Get(0);
    TensorScalarFunction copy = Serialization.copy(tensorScalarFunction);
    assertEquals(copy.apply(Tensors.vector(1, 2, 3)), RealScalar.ONE);
  }
}
