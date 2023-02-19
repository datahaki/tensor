// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.num.Pi;

class KroneckerProductTest {
  /** implementation was designed to be consistent with Mathematica
   * 
   * @param a with array structure
   * @param b with array structure
   * @return */
  @PackageTestAccess
  static Tensor KroneckerProduct_of(Tensor a, Tensor b) {
    Dimensions dim_a = new Dimensions(a);
    if (!dim_a.isArray())
      throw new Throw(a);
    Dimensions dim_b = new Dimensions(b);
    if (!dim_b.isArray())
      throw new Throw(b);
    List<Integer> list = Stream.concat( //
        dim_a.list().stream(), //
        dim_b.list().stream()).toList();
    Tensor product = TensorProduct.of(a, b);
    if (2 < list.size())
      if (list.size() % 2 == 0) {
        int half = list.size() / 2;
        for (int i = 0; i < half; ++i)
          product = Join.of(half - 1, product.stream().toList()); // general algorithm
      } else {
        if (dim_a.list().size() == 1 && dim_b.list().size() == 2) // special case: vector (X) matrix
          return Join.of(0, product.stream().toList());
        if (dim_a.list().size() == 2 && dim_b.list().size() == 1) // special case: matrix (X) vector
          return Tensor.of(product.stream().map(s -> Join.of(0, s.stream().toList())));
      }
    return product;
  }

  @Test
  void testDimensions() {
    Tensor a = Array.zeros(3, 2, 3, 4);
    Tensor b = Array.zeros(5, 6);
    assertEquals(Dimensions.of(KroneckerProduct.of(a, b)), List.of(12, 10, 18));
    assertEquals(Dimensions.of(KroneckerProduct_of(a, b)), List.of(12, 10, 18));
  }

  @Test
  void testMatrices() {
    Tensor a = ArrayReshape.of(Range.of(1, 6 + 1), 3, 2);
    Tensor b = ArrayReshape.of(Range.of(1, 20 + 1), 4, 5);
    Tensor tp1 = KroneckerProduct.of(a, b);
    Tensor tp2 = KroneckerProduct_of(a, b);
    assertEquals(Dimensions.of(tp1), List.of(12, 10));
    assertEquals(Dimensions.of(tp2), List.of(12, 10));
    assertEquals(Last.of(tp1), Tensors.vector(80, 85, 90, 95, 100, 96, 102, 108, 114, 120));
    assertEquals(Last.of(tp2), Tensors.vector(80, 85, 90, 95, 100, 96, 102, 108, 114, 120));
  }

  @Test
  void testVector2() {
    Tensor kp = KroneckerProduct_of(Tensors.vector(1, 2), Tensors.vector(3, 4, 5));
    assertEquals(kp, Tensors.fromString("{{3, 4, 5}, {6, 8, 10}}"));
  }

  @Test
  void testVecMat() {
    Tensor kp = KroneckerProduct_of(Tensors.vector(24 * 5, 36 * 5), HilbertMatrix.of(3));
    Tensor ex = Tensors.fromString("{{120, 60, 40}, {60, 40, 30}, {40, 30, 24}, {180, 90, 60}, {90, 60, 45}, {60, 45, 36}}");
    assertEquals(kp, ex);
  }

  @Test
  void testMatVec() {
    Tensor kp = KroneckerProduct_of(HilbertMatrix.of(3), Tensors.vector(24 * 5, 36 * 5));
    Tensor ex = Tensors.fromString("{{120, 180, 60, 90, 40, 60}, {60, 90, 40, 60, 30, 45}, {40, 60, 30, 45, 24, 36}}");
    assertEquals(kp, ex);
  }

  @Test
  void testScalarVec() {
    VectorQ.require(KroneckerProduct_of(Pi.VALUE, Tensors.vector(24 * 5, 36 * 5)));
    VectorQ.require(KroneckerProduct_of(Tensors.vector(24 * 5, 36 * 5), Pi.VALUE));
  }

  @Test
  void testScalarMat() {
    VectorQ.requireLength(KroneckerProduct.of(Pi.VALUE, HilbertMatrix.of(2, 3)), 6);
    VectorQ.requireLength(KroneckerProduct.of(HilbertMatrix.of(2, 3), Pi.VALUE), 6);
  }

  @Test
  void testScalarMat2() {
    MatrixQ.requireSize(KroneckerProduct_of(Pi.VALUE, HilbertMatrix.of(2, 3)), 2, 3);
    MatrixQ.requireSize(KroneckerProduct_of(HilbertMatrix.of(2, 3), Pi.VALUE), 2, 3);
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> KroneckerProduct.of(HilbertMatrix.of(2, 3), Tensors.vector(1, 2, 3)));
    Tensor nonArray = Tensors.fromString("{1,{2}}");
    assertThrows(Exception.class, () -> KroneckerProduct.of(nonArray, Tensors.vector(1, 2, 3)));
    assertThrows(Exception.class, () -> KroneckerProduct.of(Tensors.vector(1, 2, 3), nonArray));
  }
}
