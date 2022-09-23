// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class SortTest {
  @Test
  void testSort() {
    assertEquals(Sort.of(Tensors.vector(0, 4, 5, 2, -3)), Tensors.vector(-3, 0, 2, 4, 5));
    assertEquals(Sort.of(Tensors.vector(4, 5, 0, 2, -3)), Tensors.vector(-3, 0, 2, 4, 5));
    final Tensor m = Tensors.vectorDouble(0.4, 0, 0.5, 0.2, -0.3);
    assertEquals(Sort.of(m), Tensors.vectorDouble(-0.3, 0, 0.2, 0.4, 0.5));
    assertEquals(Sort.ofVector(m, Collections.reverseOrder()), Tensors.vectorDouble(0.5, 0.4, 0.2, 0, -0.3));
    assertEquals(Sort.of(m.unmodifiable(), Collections.reverseOrder()), Tensors.vectorDouble(0.5, 0.4, 0.2, 0, -0.3));
    assertEquals(Sort.of(m), Sort.of(m));
    assertEquals(Sort.of(m.unmodifiable()), Sort.of(m));
  }

  @Test
  void testSortRows() {
    Comparator<Tensor> comparator = (o1, o2) -> Scalars.compare(o1.Get(0), o2.Get(0));
    Tensor a = Tensors.fromString("{{4, 1}, {2, 8}, {9, 0}, {3, 5}}");
    Tensor s = Sort.of(a, comparator);
    assertEquals(s, Tensors.fromString("{{2, 8}, {3, 5}, {4, 1}, {9, 0}}"));
  }

  @Test
  void testStrings() {
    Tensor vector = Tensors.of( //
        StringScalar.of("c"), //
        StringScalar.of("a"), //
        StringScalar.of("b"));
    assertEquals(Sort.of(vector).toString(), "{a, b, c}");
    assertEquals(Sort.of(vector.unmodifiable()).toString(), "{a, b, c}");
  }

  @Test
  void testStringScalar() {
    Comparator<GaussScalar> comparator = Comparator.comparing(GaussScalar::prime);
    Scalar qs1 = GaussScalar.of(-3, 7);
    Scalar qs2 = GaussScalar.of(-3, 17);
    Tensor vec = Tensors.of(qs2, qs1);
    assertEquals(Sort.ofVector(vec, comparator), Tensors.of(qs1, qs2));
    assertEquals(Sort.ofVector(vec.unmodifiable(), comparator), Tensors.of(qs1, qs2));
  }

  @Test
  void testQuantity1() {
    Scalar qs1 = Quantity.of(-3, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Tensor vec = Tensors.of(qs2, qs1);
    assertEquals(Sort.of(vec), Tensors.of(qs1, qs2));
    assertEquals(Sort.ofVector(vec, Collections.reverseOrder()), Tensors.of(qs2, qs1));
  }

  @Test
  void testQuantity2() {
    Tensor vector = Tensors.of( //
        Quantity.of(0, "m"), Quantity.of(9, "m"), //
        Quantity.of(-3, "m"), Quantity.of(0, "s"), RealScalar.ZERO);
    assertThrows(Throw.class, () -> Sort.of(vector));
  }

  @Test
  void testReference() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}}");
    Tensor sorted = Sort.of(tensor);
    assertEquals(tensor, sorted);
    tensor.set(RealScalar.ONE::add, Tensor.ALL, Tensor.ALL);
    assertNotEquals(tensor, sorted);
    assertEquals(sorted, Tensors.fromString("{{1, 2, 3}}"));
    assertEquals(tensor, Tensors.fromString("{{2, 3, 4}}"));
  }

  @Test
  void testSortEmpty() {
    assertEquals(Sort.of(Tensors.empty()), Tensors.empty());
    assertEquals(Sort.ofVector(Tensors.empty(), Collections.reverseOrder()), Tensors.empty());
    assertEquals(Sort.of(Tensors.empty(), Collections.reverseOrder()), Tensors.empty());
  }

  @Test
  void testMatrix() {
    assertEquals(Sort.of(IdentityMatrix.of(3)), Tensors.fromString("{{0, 0, 1}, {0, 1, 0}, {1, 0, 0}}"));
    assertEquals(Sort.of(Tensors.fromString("{{1, 2, 4}, {1, 2}, 3}")), Tensors.fromString("{3, {1, 2}, {1, 2, 4}}"));
    assertEquals(Sort.of(Tensors.fromString("{{1, 2, 4}, {2, 1}, 5, {}}")), Tensors.fromString("{5, {}, {2, 1}, {1, 2, 4}}"));
  }

  @Test
  void testMatrixReference() {
    Tensor matrix = IdentityMatrix.of(3);
    Tensor sorted = Sort.of(matrix);
    sorted.set(RealScalar.ONE::add, Tensor.ALL, Tensor.ALL);
    assertEquals(matrix, IdentityMatrix.of(3));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> Sort.of(RealScalar.of(3.12)));
  }

  @Test
  void testScalarVectorFail() {
    assertThrows(NullPointerException.class, () -> Sort.ofVector(Tensors.vector(1, 2, 3), null));
  }
}
