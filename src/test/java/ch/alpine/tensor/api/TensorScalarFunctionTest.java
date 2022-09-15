// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.VectorNorm;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.tri.Sin;

class TensorScalarFunctionTest {
  @Test
  void testCompose() throws ClassNotFoundException, IOException {
    TensorScalarFunction tsf = VectorNorm.of(2).compose(Times.operator(Tensors.vector(1, 2, 3)));
    Serialization.copy(tsf);
    Scalar scalar = tsf.apply(Tensors.vector(3, 2, 2));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(7.810249675906654));
  }

  @Test
  void testAndThen() throws ClassNotFoundException, IOException {
    TensorScalarFunction tsf = VectorNorm.of(2).andThen(Sin.FUNCTION);
    Serialization.copy(tsf);
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
