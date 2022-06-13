// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class AccumulateTest {
  @Test
  void testEmpty() {
    assertEquals(Accumulate.of(Tensors.empty()), Tensors.empty());
  }

  @Test
  void testVector() {
    Tensor vector = Tensors.vector(2, 3, 1, 0);
    assertEquals(Accumulate.of(vector), Tensors.vector(2, 5, 6, 6));
  }

  @Test
  void testProd() {
    Tensor vector = Tensors.vector(2, 3, -1, 0);
    assertEquals(Accumulate.prod(vector), Tensors.vector(2, 6, -6, 0));
  }

  @Test
  void testMatrixAdd() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2 }, { 5, 5 }, { -3, -9 } });
    Tensor actual = Accumulate.of(matrix);
    Tensor expected = Tensors.matrix(new Number[][] { { 1, 2 }, { 6, 7 }, { 3, -2 } });
    assertEquals(expected, actual);
  }

  @Test
  void testMatrixProd() {
    Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2 }, { 5, 5 }, { -3, -9 } });
    assertEquals(Accumulate.prod(matrix), Tensors.fromString("{{1, 2}, {5, 10}, {-15, -90}}"));
  }

  @Test
  void testProdAd() {
    Tensor tensor = Accumulate.prod(Array.zeros(3, 3, 3));
    assertEquals(Dimensions.of(tensor), List.of(3, 3, 3));
  }

  @Test
  void testScalarFail() {
    assertThrows(IllegalArgumentException.class, () -> Accumulate.of(RealScalar.ONE));
  }

  @Test
  void testScalarProdFail() {
    assertThrows(IllegalArgumentException.class, () -> Accumulate.prod(RealScalar.ONE));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> Accumulate.of(null));
  }

  @Test
  void testNullProdFail() {
    assertThrows(NullPointerException.class, () -> Accumulate.prod(null));
  }
}
