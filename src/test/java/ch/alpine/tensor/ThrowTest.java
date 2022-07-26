// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;

class ThrowTest {
  @Test
  void testFull() throws ClassNotFoundException, IOException {
    Exception exception = Serialization.copy(new Throw(Tensors.vector(1, 2), Tensors.vector(9, 3)));
    assertEquals(exception.getMessage(), "Throw[{1, 2}, {9, 3}]");
  }

  @Test
  void testFullScalar() {
    Exception exception = new Throw(Tensors.vector(1, 2), RationalScalar.HALF, Tensors.empty());
    assertEquals(exception.getMessage(), "Throw[{1, 2}, 1/2, {}]");
  }

  @Test
  void testObject() {
    assertEquals(new Throw("abc").getMessage(), "Throw[abc]");
  }

  @Test
  void testShort() {
    Exception exception = new Throw(Array.zeros(20, 10, 5), RealScalar.ONE);
    assertEquals(exception.getMessage(), "Throw[T[20, 10, 5], 1]");
  }

  @Test
  void testEmpty() {
    assertEquals(new Throw().getMessage(), "Throw[]");
  }

  @Test
  void testSerializable() throws Exception {
    Exception exception = Serialization.copy(new Throw(RealScalar.ONE));
    assertEquals(exception.getMessage(), "Throw[1]");
  }

  @Test
  void testMessage() {
    Exception exception = new Throw(Pi.VALUE);
    assertEquals(exception.getMessage(), "Throw[3.141592653589793]");
  }

  @Test
  void testNull() {
    Exception exception = new Throw(Tensors.vector(4, 7, 1, 1), null, RealScalar.ONE);
    assertEquals(exception.getMessage(), "Throw[{4, 7, 1, 1}, null, 1]");
  }
}
