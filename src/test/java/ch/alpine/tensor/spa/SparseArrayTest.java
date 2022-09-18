// code by jph
package ch.alpine.tensor.spa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;

class SparseArrayTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Tensor tensor = Serialization.copy(SparseArray.of(RealScalar.ZERO, 5, 6, 8));
    Tensor value = tensor.get(1);
    assertEquals(value.length(), 6);
    Tensor copy = tensor.copy();
    assertEquals(copy.length(), 5);
    assertEquals(tensor.get(1, 2, 3), RealScalar.ZERO);
    Tensor fullze = Array.zeros(5, 6, 8);
    assertEquals(tensor, fullze);
    assertEquals(fullze, tensor);
    Tensor tinsor = SparseArray.of(RealScalar.ZERO, 5, 6, 8);
    assertEquals(tensor, tinsor);
  }

  @Test
  void testScalar() {
    assertEquals(Array.zeros(), RealScalar.ZERO);
    assertEquals(SparseArray.of(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testVector() {
    Tensor matrix = SparseArray.of(RealScalar.ZERO, 100);
    assertEquals(matrix.Get(99), RealScalar.ZERO);
    assertThrows(IllegalArgumentException.class, () -> matrix.get(-1));
    assertThrows(IllegalArgumentException.class, () -> matrix.get(100));
    assertThrows(IllegalArgumentException.class, () -> matrix.Get(100));
  }

  @Test
  void testDot() {
    Tensor s_mat1 = SparseArray.of(RealScalar.ZERO, 3, 4);
    Tensor f_mat1 = Array.zeros(3, 4);
    Tensor s_mat2 = SparseArray.of(RealScalar.ZERO, 4, 2);
    Tensor f_mat2 = Array.zeros(4, 2);
    s_mat1.dot(f_mat2);
    s_mat1.dot(s_mat2);
    f_mat1.dot(f_mat2);
    f_mat1.dot(s_mat2);
  }

  @Test
  void testHashCode() {
    Tensor sparse = SparseArray.of(RealScalar.ZERO, 5, 4);
    Tensor matrix = Array.zeros(5, 4);
    assertEquals(sparse.hashCode(), matrix.hashCode());
  }

  @Test
  void testHashCode2() {
    Tensor sparse = SparseArray.of(RealScalar.ZERO, 5, 4);
    sparse.set(RationalScalar.HALF, 1, 2);
    Tensor matrix = Array.zeros(5, 4);
    matrix.set(RationalScalar.HALF, 1, 2);
    assertEquals(sparse.hashCode(), matrix.hashCode());
  }

  @Test
  void testArrayGet() {
    Tensor sparse = SparseArray.of(GaussScalar.of(0, 5), 5, 4, 8);
    sparse.set(GaussScalar.of(3, 5), 0, 1, 2);
    assertEquals(sparse.get(1, Tensor.ALL, 3), ConstantArray.of(GaussScalar.of(0, 5), 4));
    assertThrows(IllegalArgumentException.class, () -> sparse.get(Tensor.ALL));
    Tensor result = sparse.get(Tensor.ALL, 2);
    assertInstanceOf(SparseArray.class, result);
    assertEquals(result, Normal.of(sparse).get(Tensor.ALL, 2));
    assertEquals(sparse.get(Tensor.ALL, 2, Tensor.ALL), Normal.of(sparse).get(Tensor.ALL, 2));
  }

  @Test
  void testFails() {
    Tensor sparse = SparseArray.of(GaussScalar.of(0, 5), 5, 4, 8);
    assertThrows(UnsupportedOperationException.class, () -> sparse.unmodifiable());
    assertThrows(UnsupportedOperationException.class, () -> sparse.append(Array.zeros(4, 8)));
    assertThrows(Throw.class, () -> sparse.map(RealScalar.ONE::add));
  }

  @Test
  void testMatrix() {
    Tensor matrix = SparseArray.of(RealScalar.ZERO, 5, 10);
    assertEquals(matrix.Get(3, 2), RealScalar.ZERO);
    matrix.set(Pi.VALUE, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> matrix.set(RealScalar.ONE, 5, 3));
    assertThrows(IllegalArgumentException.class, () -> matrix.set(RealScalar.ONE, 2, 10));
    assertEquals(matrix.get(2, 3), Pi.VALUE);
    Tensor minus = matrix.negate();
    assertInstanceOf(SparseArray.class, minus);
    assertEquals(minus.get(2, 3), Pi.VALUE.negate());
    Tensor grid = matrix.add(Array.zeros(5, 10));
    assertEquals(Total.ofVector(Total.of(grid)), Pi.VALUE);
    Tensor result = minus.multiply(RealScalar.of(-4));
    assertEquals(result.get(2, 3), Pi.VALUE.multiply(RealScalar.of(4)));
  }

  @Test
  void testExtract() {
    Tensor tensor = SparseArray.of(RealScalar.ZERO, 5, 8, 7);
    tensor.set(Pi.TWO, 2, 3, 4);
    assertEquals(Dimensions.of(tensor.extract(1, 3)), Arrays.asList(2, 8, 7));
    for (int count = 0; count < tensor.length(); ++count)
      assertEquals(tensor.extract(count, count), Tensors.empty());
  }

  @Test
  void testExtract2() {
    Tensor tensor = SparseArray.of(RealScalar.ZERO, 5);
    assertEquals(tensor.extract(0, 5), Array.zeros(5));
    assertEquals(tensor.extract(3, 5), Array.zeros(2));
    assertEquals(tensor.extract(3, 3), Array.zeros(0));
    assertThrows(IllegalArgumentException.class, () -> tensor.extract(-1, 3));
    assertThrows(IllegalArgumentException.class, () -> tensor.extract(1, 6));
  }

  @Test
  void testCreateScalar() {
    assertEquals(TestHelper.of(Pi.VALUE), Pi.VALUE);
  }

  @Test
  void testCreate() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    assertEquals(Dimensions.of(sparse), Arrays.asList(3, 5));
    assertEquals(Pretty.of(sparse), Pretty.of(tensor));
    sparse.toString();
    Tensor result = sparse.divide(RationalScalar.HALF);
    assertEquals(result, tensor.multiply(RealScalar.TWO));
    ExactTensorQ.require(sparse);
    assertInstanceOf(SparseArray.class, result);
  }

  @Test
  void testBlock0() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    assertEquals(Normal.of(sparse), tensor);
    Tensor result = sparse.block(Arrays.asList(1, 0), Arrays.asList(2, 3));
    Tensor expect = Tensors.fromString("{{5, 6, 8}, {0, 2, 9}}");
    assertEquals(Dimensions.of(result), Arrays.asList(2, 3));
    assertEquals(result, expect);
    Tensor mapped = result.map(s -> s);
    assertEquals(mapped, expect);
    assertInstanceOf(SparseArray.class, mapped);
    assertEquals(Normal.of(result), expect);
  }

  @Test
  void testBlock1() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    Tensor result = sparse.block(Arrays.asList(1, 0), Arrays.asList(0, 0));
    assertEquals(result, Tensors.empty());
    assertEquals(Dimensions.of(result), Arrays.asList(0));
  }

  @Test
  void testBlock2() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    Tensor result = sparse.block(Arrays.asList(2), Arrays.asList(1));
    assertEquals(result, tensor.block(Arrays.asList(2), Arrays.asList(1)));
    assertEquals(Dimensions.of(result), Arrays.asList(1, 5));
    MatrixQ.require(result);
  }

  @Test
  void testCreateId() {
    Tensor sparse = TestHelper.of(IdentityMatrix.of(5));
    SquareMatrixQ.require(sparse);
    assertEquals(Inverse.of(sparse), IdentityMatrix.of(5));
  }

  @Test
  void testSetSimple() {
    Tensor tensor = Tensors.vector(1, 0, 3, 0, 0);
    Tensor sparse = TestHelper.of(tensor);
    assertEquals(Normal.of(sparse), tensor);
    sparse.set(RealScalar.ONE::add, Tensor.ALL);
    assertEquals(sparse, Tensors.vector(2, 1, 4, 1, 1));
    sparse.set(RealScalar.TWO::add, 1);
    assertEquals(sparse, Tensors.vector(2, 3, 4, 1, 1));
    assertInstanceOf(SparseArray.class, sparse);
    assertThrows(Throw.class, () -> sparse.set(Tensors.vector(3), 2));
  }

  @Test
  void testSet1All() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    tensor.set(Tensors.vector(-1, -2, -3, -4, -5), 1, Tensor.ALL);
    sparse.set(Tensors.vector(-1, -2, -3, -4, -5), 1, Tensor.ALL);
    assertEquals(tensor, sparse);
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2, -3, -4), 1, Tensor.ALL));
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2, -3, -4, -5), 3, Tensor.ALL));
  }

  @Test
  void testSetAll4() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = TestHelper.of(tensor);
    tensor.set(Tensors.vector(-1, -2, -3), Tensor.ALL, 4);
    sparse.set(Tensors.vector(-1, -2, -3), Tensor.ALL, 4);
    assertEquals(tensor, sparse);
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2, -3), Tensor.ALL, 5));
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2), Tensor.ALL, 4));
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2, 3, 4), Tensor.ALL, 4));
  }

  @Test
  void testSetAll4Zeros() {
    Tensor tensor = Array.zeros(3, 5);
    Tensor sparse = SparseArray.of(RealScalar.ZERO, 3, 5);
    tensor.set(Tensors.vector(-1, -2, -3), Tensor.ALL, 4);
    sparse.set(Tensors.vector(-1, -2, -3), Tensor.ALL, 4);
    assertEquals(tensor, sparse);
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2, -3), Tensor.ALL, 5));
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2), Tensor.ALL, 4));
    assertThrows(IllegalArgumentException.class, () -> sparse.set(Tensors.vector(-1, -2, 3, 4), Tensor.ALL, 4));
  }

  @Test
  void testSetRemove() {
    Tensor tensor = Tensors.vector(1, 0, 3, 0, 0);
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(RealScalar.of(-3)::add, 2);
    assertEquals(sparse, Tensors.vector(1, 0, 0, 0, 0));
    assertInstanceOf(SparseArray.class, sparse);
  }

  @Test
  void testSetMatrix() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4},{0,0,0,0,0}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(RealScalar.ONE::add, 1, Tensor.ALL);
    Tensor expect = Tensors.fromString("{{1,0,3,0,0},{6,7,9,1,1},{0,2,9,0,4},{0,0,0,0,0}}");
    assertEquals(sparse, expect);
    sparse.set(RealScalar.TWO::add, 3, 2);
    Tensor expecu = Tensors.fromString("{{1,0,3,0,0},{6,7,9,1,1},{0,2,9,0,4},{0,0,2,0,0}}");
    assertEquals(sparse, expecu);
  }

  @Test
  void testSetMatrixAll() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4},{0,0,0,0,0}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(RealScalar.TWO::add, Tensor.ALL, 1);
    Tensor expect = Tensors.fromString("{{1,2,3,0,0},{5,8,8,0,0},{0,4,9,0,4},{0,2,0,0,0}}");
    assertEquals(sparse, expect);
  }

  @Test
  void testSetMatrixVector() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4},{0,0,0,0,0}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(tensor.get(2)::add, 1);
    Tensor expect = Tensors.fromString("{{1,0,3,0,0},{5,8,17,0,4},{0,2,9,0,4},{0,0,0,0,0}}");
    assertEquals(sparse, expect);
  }

  @Test
  void testSetMatrixRemove() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4},{0,0,0,0,0}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(tensor.get(2).negate()::add, 2);
    Tensor expect = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,0,0,0,0},{0,0,0,0,0}}");
    assertEquals(sparse, expect);
  }

  @Test
  void testSetMatrixFull() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4},{0,0,0,0,0}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(Tensors.vector(1, 2, 3, 4, 5), 2);
    Tensor expect = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{1,2,3,4,5},{0,0,0,0,0}}");
    assertEquals(sparse, expect);
    SparseArray sa = (SparseArray) sparse;
    assertInstanceOf(SparseArray.class, sa.byRef(1));
    assertInstanceOf(SparseArray.class, sa.byRef(2));
  }

  @Test
  void testSetFail() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4},{0,0,0,0,0}}");
    Tensor sparse = TestHelper.of(tensor);
    sparse.set(Tensors.vector(1, 2, 3, 4, 5), 2);
    sparse.set(Array.zeros(5), 2);
    assertThrows(Throw.class, () -> sparse.set(Array.zeros(5, 2), 2));
    assertThrows(Throw.class, () -> sparse.set(Tensors.fromString("{1,2,{3},4,5}"), 2));
  }

  @Test
  void testDotFull() {
    Tensor tensor = LeviCivitaTensor.of(3).dot(HilbertMatrix.of(3));
    tensor.toString();
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3, 3));
  }

  @Test
  void testQuantity() {
    Tensor tensor = LeviCivitaTensor.of(3);
    assertInstanceOf(SparseArray.class, tensor);
    tensor = tensor.multiply(Quantity.of(0, "m"));
    assertEquals(tensor.toString(), "SparseArray[{}, {3, 3, 3}, 0[m]]");
  }

  @Test
  void testQuantityOps() {
    String string = "{{3[m], 7[m], 0[m]}, {1[m], 0[m], 0[m]}}";
    Tensor full = Tensors.fromString(string);
    SparseArray sparseArray = (SparseArray) TestHelper.of(full);
    assertEquals(full, sparseArray);
    int nnz = Nnz.of(sparseArray);
    assertEquals(nnz, 3);
    Tensor tensor = Normal.of(sparseArray);
    assertEquals(tensor.toString(), string);
    Tensor dot = sparseArray.dot(HilbertMatrix.of(3));
    assertInstanceOf(SparseArray.class, dot);
  }

  @Test
  void testFallbackScalarFail() {
    assertEquals(SparseArray.of(Quantity.of(0, "A")), Quantity.of(0, "A"));
    assertThrows(Throw.class, () -> SparseArray.of(Pi.VALUE));
  }

  @Test
  void testFail() {
    Tensor tensor = Tensors.fromString("{1,0,{3},0,0}");
    assertThrows(ClassCastException.class, () -> TestHelper.of(tensor));
  }
}
