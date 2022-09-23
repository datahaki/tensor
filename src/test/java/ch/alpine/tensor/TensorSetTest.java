// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.red.Total;

class TensorSetTest {
  @Test
  void testSetScalar() {
    Tensor eye = IdentityMatrix.of(5);
    Tensor cpy = eye.copy();
    assertEquals(eye, cpy);
    cpy.set(DoubleScalar.of(0.3), 1, 2);
    assertFalse(eye.equals(cpy));
    cpy.set(s -> s.negate(), 2, 2);
  }

  @Test
  void testSetTensor0() {
    Tensor eye = HilbertMatrix.of(3);
    Tensor matrix = eye.copy();
    assertEquals(eye, matrix);
    matrix.set(Tensor::negate, 2);
    assertFalse(eye.equals(matrix));
    assertEquals(matrix, Tensors.fromString("{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}, {-1/3, -1/4, -1/5}}"));
  }

  @Test
  void testSetTensor0Total() {
    Tensor eye = HilbertMatrix.of(3);
    Tensor matrix = eye.copy();
    assertEquals(eye, matrix);
    matrix.set(Total::ofVector, 2);
    assertFalse(eye.equals(matrix));
    assertEquals(matrix, Tensors.fromString("{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}, 47/60}"));
    assertThrows(Throw.class, () -> eye.set(Total::ofVector, Tensor.ALL, 2));
  }

  @Test
  void testSetTensor1() {
    Tensor eye = HilbertMatrix.of(3);
    Tensor cpy = eye.copy();
    assertEquals(eye, cpy);
    cpy.set(Tensor::negate, Tensor.ALL, 2);
    assertFalse(eye.equals(cpy));
    assertEquals(cpy, Tensors.fromString("{{1, 1/2, -1/3}, {1/2, 1/3, -1/4}, {1/3, 1/4, -1/5}}"));
  }

  @Test
  void testSetNotByRef() {
    Tensor a = Tensors.vector(1);
    Tensor row = Tensors.vector(1, 2, 3, 4);
    a.set(row, 0);
    Tensor copy = a.copy();
    row.set(s -> s.multiply(RealScalar.ZERO), 2);
    assertEquals(a, copy);
  }

  @Test
  void testSetAllLast() {
    Tensor a = Tensors.vector(0, 1, 3, 4);
    Tensor row = Tensors.vector(5, 6, 7, 8);
    a.set(row, Tensor.ALL);
    assertEquals(a, row);
  }

  @Test
  void testSetMultiAll0() {
    Tensor a = Array.zeros(2, 3);
    Tensor b = Array.zeros(2, 1);
    a.set(b, Tensor.ALL, 2);
    Tensor c = Tensors.fromString("{{0, 0, {0}}, {0, 0, {0}}}");
    assertEquals(a, c);
  }

  @Test
  void testSetMultiAll1() {
    Tensor a = Array.zeros(2, 3, 4, 5);
    Tensor b = Array.zeros(3, 5, 1);
    a.set(b, 1, Tensor.ALL, 2, Tensor.ALL);
    assertFalse(ArrayQ.of(a));
    List<Integer> dims = Dimensions.of(a.get(1, Tensor.ALL, 2, Tensor.ALL));
    assertEquals(dims, Dimensions.of(b));
  }

  @Test
  void testSetMultiAll2() {
    Tensor a = Array.zeros(2, 3, 4, 5);
    Tensor b = Array.zeros(2, 4, 1);
    a.set(b, Tensor.ALL, 2, Tensor.ALL, 2);
    assertFalse(ArrayQ.of(a));
    List<Integer> dims = Dimensions.of(a.get(Tensor.ALL, 2, Tensor.ALL, 2));
    assertEquals(dims, Dimensions.of(b));
  }

  @Test
  void testSetAllFail() {
    Tensor a = Tensors.vector(0, 1, 3, 4);
    Tensor c = a.copy();
    assertThrows(IllegalArgumentException.class, () -> a.set(Tensors.vector(5, 6, 7, 8, 9), Tensor.ALL));
    assertEquals(a, c);
    assertThrows(IllegalArgumentException.class, () -> a.set(Tensors.vector(5, 6, 7), Tensor.ALL));
    assertEquals(a, c);
  }

  @Test
  void testSet333() {
    Tensor ad = Array.zeros(3, 3, 3);
    Tensor mat = HilbertMatrix.of(3);
    ad.set(mat, Tensor.ALL, 2);
    assertEquals(Dimensions.of(ad), Arrays.asList(3, 3, 3));
    assertEquals(ad.get(Tensor.ALL, 2), mat);
  }

  @Test
  void testSetSelf() {
    Tensor a = Tensors.vector(1); // 1
    assertEquals(TensorRank.of(a), 1);
    a.set(a, 0);
    assertEquals(TensorRank.of(a), 2);
    a.set(a, Tensor.ALL);
    assertEquals(TensorRank.of(a), 2);
    a.set(a, 0);
    assertEquals(TensorRank.of(a), 3);
    a.set(a, Tensor.ALL);
    a.set(a, 0);
    a.set(a, 0);
    assertEquals(TensorRank.of(a), 5);
  }

  @Test
  void testSetAllSelfVector() {
    Tensor a = Range.of(10, 20);
    a.set(a, Tensor.ALL);
    assertEquals(a, Range.of(10, 20));
  }

  @Test
  void testSetAllSelfMatrix() {
    Tensor a = HilbertMatrix.of(3, 5);
    a.set(a, Tensor.ALL);
    assertEquals(a, HilbertMatrix.of(3, 5));
    a.set(a, Tensor.ALL, Tensor.ALL);
    assertEquals(a, HilbertMatrix.of(3, 5));
  }

  @Test
  void testSetAllSelfUnstructured() {
    Tensor a = Tensors.fromString("{{1, 2}, {3+I, 4, 5, 6}, {}}");
    Tensor c = a.copy().unmodifiable();
    a.set(a, Tensor.ALL);
    assertEquals(a, c);
    a.set(a, Tensor.ALL, Tensor.ALL);
    assertEquals(a, c);
  }

  @Test
  void testSetFunctionAllLast() {
    Tensor a = Tensors.vector(0, 1, 3, 4, 9);
    a.set(RealScalar.ONE::add, Tensor.ALL);
    Tensor b = Tensors.vector(1, 2, 4, 5, 10);
    assertEquals(a, b);
  }

  @Test
  void testSetAll() {
    Tensor matrix = HilbertMatrix.of(4, 6);
    List<Integer> l1 = Dimensions.of(matrix);
    Tensor column = Tensors.vector(3, 2, 1, 0);
    matrix.set(column, Tensor.ALL, 2);
    assertEquals(matrix.get(Tensor.ALL, 2), column);
    List<Integer> l2 = Dimensions.of(matrix);
    assertEquals(l1, l2);
  }

  @Test
  void testSetAllBlob() {
    Tensor vector = Tensors.vector(3, 4, 5, 6);
    vector.set(HilbertMatrix.of(2), 2);
    assertEquals(vector.toString(), "{3, 4, {{1, 1/2}, {1/2, 1/3}}, 6}");
  }

  @Test
  void testSetFunctionAll() {
    Tensor matrix = HilbertMatrix.of(4, 6);
    Tensor column = Tensors.vector(3, 4, 5, 6);
    matrix.set(Scalar::reciprocal, Tensor.ALL, 2);
    assertEquals(matrix.get(Tensor.ALL, 2), column);
  }

  @Test
  void testSetTensorLevel0() {
    Tensor vector = Tensors.vector(2, 3, 4);
    assertThrows(IllegalArgumentException.class, () -> vector.set(RealScalar.ONE));
  }

  @Test
  void testSetFunctionLevel0() {
    Tensor vector = Tensors.vector(2, 3, 4);
    assertThrows(IllegalArgumentException.class, () -> vector.set(t -> RealScalar.ONE));
  }
}
