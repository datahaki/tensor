// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;

class ArrayQTest {
  @Test
  void testScalar() {
    assertTrue(ArrayQ.of(RealScalar.ONE));
    assertTrue(ArrayQ.of(ComplexScalar.fromPolar(3.7, 9.8)));
    assertTrue(ArrayQ.of(Quantity.of(4, "m")));
  }

  @Test
  void testIsArray() {
    Tensor d = DoubleScalar.of(0.12);
    assertTrue(ArrayQ.of(d));
    assertTrue(ArrayQ.of(Tensors.empty()));
    Tensor a = Tensors.vectorLong(3, 2, 3);
    assertTrue(ArrayQ.of(a));
    Tensor b = Tensors.vectorLong(3, 2);
    Tensor c = Tensors.of(a, b);
    assertFalse(ArrayQ.of(c));
  }

  @ParameterizedTest
  @ValueSource(ints = { 0, 1, 2, 3, 4 })
  void testOfRank(int rank) {
    assertEquals(rank == 0, ArrayQ.ofRank(RealScalar.ONE, rank));
    assertEquals(rank == 1, ArrayQ.ofRank(Tensors.vector(1, 2, 3), rank));
    assertEquals(rank == 2, ArrayQ.ofRank(HilbertMatrix.of(2, 3), rank));
    assertEquals(rank == 3, ArrayQ.ofRank(Array.zeros(3, 4, 5), rank));
  }

  @Test
  void testRequire() {
    Tensor tensor = Tensors.fromString("{{1, 2}, 3}");
    assertThrows(TensorRuntimeException.class, () -> ArrayQ.require(tensor));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> ArrayQ.of(null));
  }
}
