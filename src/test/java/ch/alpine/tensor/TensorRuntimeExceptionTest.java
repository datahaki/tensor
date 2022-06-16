// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;

class TensorRuntimeExceptionTest {
  @Test
  void testFull() throws ClassNotFoundException, IOException {
    Exception exception = Serialization.copy(TensorRuntimeException.of(Tensors.vector(1, 2), Tensors.vector(9, 3)));
    assertEquals(exception.getMessage(), "{1, 2}; {9, 3}");
  }

  @Test
  void testFullScalar() {
    Exception exception = TensorRuntimeException.of(Tensors.vector(1, 2), RationalScalar.HALF, Tensors.empty());
    assertEquals(exception.getMessage(), "{1, 2}; 1/2; {}");
  }

  @Test
  void testShort() {
    Exception exception = TensorRuntimeException.of(Array.zeros(20, 10, 5), RealScalar.ONE);
    assertEquals(exception.getMessage(), "T[20, 10, 5]; 1");
  }

  @Test
  void testEmpty() {
    Exception exception = TensorRuntimeException.of();
    assertEquals(exception.getMessage(), "");
  }

  @Test
  void testSerializable() throws Exception {
    Exception exception = Serialization.copy(TensorRuntimeException.of(RealScalar.ONE));
    assertEquals(exception.getMessage(), "1");
  }

  @Test
  void testMessage() {
    Exception exception = TensorRuntimeException.of(Pi.VALUE);
    assertEquals(exception.getMessage(), "3.141592653589793");
  }

  @Test
  void testNull() {
    Exception exception = TensorRuntimeException.of(Tensors.vector(4, 7, 1, 1), null, RealScalar.ONE);
    assertEquals(exception.getMessage(), "{4, 7, 1, 1}; null; 1");
  }
}
