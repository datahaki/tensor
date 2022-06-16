// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

class TensorRankTest {
  @Test
  void testRank0() {
    Tensor a = DoubleScalar.of(2.32123);
    assertEquals(TensorRank.of(a), 0);
  }

  @Test
  void testRank1() {
    Tensor a = Tensors.empty();
    assertEquals(TensorRank.of(a), 1);
    assertEquals(TensorRank.of(Tensors.vector(1, 2, 3)), 1);
  }

  @Test
  void testRank12() {
    Tensor a = Tensors.vectorLong(3, 2, 3);
    assertEquals(TensorRank.of(a), 1);
    Tensor b = Tensors.vectorLong(3, 2, 9);
    Tensor d = Tensors.of(a, b);
    assertEquals(TensorRank.of(d), 2);
  }

  @Test
  void testRank01() {
    Tensor a = DoubleScalar.of(2.32123);
    assertEquals(TensorRank.of(a), 0);
    Tensor b = Tensors.vectorLong(3, 2);
    assertEquals(TensorRank.of(b), 1);
    Tensor c = DoubleScalar.of(1.23);
    assertEquals(TensorRank.of(c), 0);
    Tensor d = Tensors.of(a, b, c);
    assertEquals(TensorRank.of(d), 1);
  }

  @Test
  void testRank11() {
    Tensor a = Tensors.vectorLong(1, 3, 2);
    Tensor b = Tensors.vectorLong(3, 2);
    Tensor c = Tensors.of(a, b);
    assertEquals(TensorRank.of(c), 1);
  }

  @Test
  void testRank3() {
    assertEquals(TensorRank.of(Array.zeros(5, 4, 3)), 3);
  }

  @Test
  void testOfArray() {
    assertEquals(TensorRank.ofArray(Tensors.fromString("{1, 2}")), 1);
    assertEquals(TensorRank.ofArray(Tensors.fromString("123")), 0);
    assertEquals(TensorRank.ofArray(Tensors.fromString("{{1, 2}}")), 2);
  }

  @Test
  void testOfArrayFail() {
    assertThrows(TensorRuntimeException.class, () -> TensorRank.ofArray(Tensors.fromString("{{1}, 2}")));
    assertThrows(TensorRuntimeException.class, () -> TensorRank.ofArray(Tensors.fromString("{{1, 2}, {2}}")));
  }
}
