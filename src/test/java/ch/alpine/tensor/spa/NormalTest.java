// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;

class NormalTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensors.fromString("{{1}, 2}");
    Tensor result = Normal.of(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testScalar() {
    assertEquals(Normal.of(Pi.VALUE), Pi.VALUE);
  }

  @Test
  void testMixed() {
    Tensor mixed = Tensors.of( //
        SparseArray.of(RealScalar.ZERO, 3), Tensors.vector(1, 2), SparseArray.of(RealScalar.ZERO, 3));
    assertInstanceOf(SparseArray.class, mixed.get(0));
    assertEquals(Normal.of(mixed).toString(), "{{0, 0, 0}, {1, 2}, {0, 0, 0}}");
  }

  @SuppressWarnings("unchecked")
  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(new Normal((Function<Scalar, ? extends Tensor> & Serializable) s -> s.add(s)));
  }
}
