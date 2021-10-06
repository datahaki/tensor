// code by jph
package ch.alpine.tensor;

import java.io.IOException;
import java.util.Arrays;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SparseArrayTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor tensor = Serialization.copy(new SparseArray(Arrays.asList(5, 6, 8), RealScalar.ZERO));
    Tensor value = tensor.get(1);
    assertEquals(value.length(), 6);
    Tensor copy = tensor.copy();
    assertEquals(copy.length(), 5);
    assertEquals(tensor.get(1, 2, 3), RealScalar.ZERO);
    Tensor fullze = Array.zeros(5, 6, 8);
    assertEquals(tensor, fullze);
    assertEquals(fullze, tensor);
    Tensor tinsor = new SparseArray(Arrays.asList(5, 6, 8), RealScalar.ZERO);
    assertEquals(tensor, tinsor);
  }

  public void testVector() {
    Tensor matrix = new SparseArray(Arrays.asList(100), RealScalar.ZERO);
    assertEquals(matrix.Get(99), RealScalar.ZERO);
    AssertFail.of(() -> matrix.get(-1));
    AssertFail.of(() -> matrix.get(100));
    AssertFail.of(() -> matrix.Get(100));
  }

  public void testDot() {
    Tensor s_mat1 = new SparseArray(Arrays.asList(3, 4), RealScalar.ZERO);
    Tensor f_mat1 = Array.zeros(3, 4);
    Tensor s_mat2 = new SparseArray(Arrays.asList(4, 2), RealScalar.ZERO);
    Tensor f_mat2 = Array.zeros(4, 2);
    s_mat1.dot(f_mat2);
    s_mat1.dot(s_mat2);
    f_mat1.dot(f_mat2);
    f_mat1.dot(s_mat2);
  }

  public void testHashCode() {
    Tensor sparse = new SparseArray(Arrays.asList(5, 4), RealScalar.ZERO);
    Tensor matrix = Array.zeros(5, 4);
    assertEquals(sparse.hashCode(), matrix.hashCode());
  }

  public void testHashCode2() {
    Tensor sparse = new SparseArray(Arrays.asList(5, 4), RealScalar.ZERO);
    sparse.set(RationalScalar.HALF, 1, 2);
    Tensor matrix = Array.zeros(5, 4);
    matrix.set(RationalScalar.HALF, 1, 2);
    assertEquals(sparse.hashCode(), matrix.hashCode());
  }

  public void testArrayGet() {
    Tensor array = new SparseArray(Arrays.asList(5, 10, 8), Quantity.of(0, "m"));
    assertEquals(array.get(1, Tensor.ALL, 3), ConstantArray.of(Quantity.of(0, "m"), 10));
  }

  public void testMatrix() {
    Tensor matrix = new SparseArray(Arrays.asList(5, 10), RealScalar.ZERO);
    assertEquals(matrix.Get(3, 2), RealScalar.ZERO);
    matrix.set(Pi.VALUE, 2, 3);
    AssertFail.of(() -> matrix.set(RealScalar.ONE, 5, 3));
    AssertFail.of(() -> matrix.set(RealScalar.ONE, 2, 10));
    assertEquals(matrix.get(2, 3), Pi.VALUE);
    Tensor minus = matrix.negate();
    assertTrue(minus instanceof SparseArray);
    assertEquals(minus.get(2, 3), Pi.VALUE.negate());
    Tensor grid = matrix.add(Array.zeros(5, 10));
    assertEquals(Total.ofVector(Total.of(grid)), Pi.VALUE);
    Tensor result = minus.multiply(RealScalar.of(-4));
    assertEquals(result.get(2, 3), Pi.VALUE.multiply(RealScalar.of(4)));
  }

  public void testExtract() {
    Tensor tensor = new SparseArray(Arrays.asList(5, 8, 7), RealScalar.ZERO);
    tensor.set(Pi.TWO, 2, 3, 4);
    assertEquals(Dimensions.of(tensor.extract(1, 3)), Arrays.asList(2, 8, 7));
  }

  public void testCreateScalar() {
    assertEquals(SparseArrays.of(Pi.VALUE, RealScalar.ZERO), Pi.VALUE);
  }

  public void testCreate() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{0,0,0,0,0},{0,2,0,0,4}}");
    Tensor sparse = SparseArrays.of(tensor, RealScalar.ZERO);
    assertEquals(Dimensions.of(sparse), Arrays.asList(3, 5));
    assertEquals(Pretty.of(sparse), Pretty.of(tensor));
    sparse.toString();
    Tensor result = sparse.divide(RationalScalar.HALF);
    assertEquals(result, tensor.multiply(RealScalar.TWO));
    ExactTensorQ.require(sparse);
    assertTrue(result instanceof SparseArray);
  }

  public void testBlock0() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = SparseArrays.of(tensor, RealScalar.ZERO);
    Tensor result = sparse.block(Arrays.asList(1, 0), Arrays.asList(2, 3));
    Tensor expect = Tensors.fromString("{{5, 6, 8}, {0, 2, 9}}");
    assertEquals(Dimensions.of(result), Arrays.asList(2, 3));
    assertEquals(result, expect);
    Tensor mapped = result.map(s -> s);
    assertEquals(mapped, expect);
    assertTrue(mapped instanceof SparseArray);
    Tensor full = Array.of(result::get, Dimensions.of(result));
    assertEquals(full, expect);
  }

  public void testBlock1() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = SparseArrays.of(tensor, RealScalar.ZERO);
    Tensor result = sparse.block(Arrays.asList(1, 0), Arrays.asList(0, 0));
    assertEquals(result, Tensors.empty());
    assertEquals(Dimensions.of(result), Arrays.asList(0));
  }

  public void testBlock2() {
    Tensor tensor = Tensors.fromString("{{1,0,3,0,0},{5,6,8,0,0},{0,2,9,0,4}}");
    Tensor sparse = SparseArrays.of(tensor, RealScalar.ZERO);
    Tensor result = sparse.block(Arrays.asList(2), Arrays.asList(1));
    assertEquals(result, tensor.block(Arrays.asList(2), Arrays.asList(1)));
    assertEquals(Dimensions.of(result), Arrays.asList(1, 5));
    MatrixQ.require(result);
  }

  public void testCreateId() {
    Tensor sparse = SparseArrays.of(IdentityMatrix.of(5), RealScalar.ZERO);
    SquareMatrixQ.require(sparse);
    assertEquals(Inverse.of(sparse), IdentityMatrix.of(5));
  }
}
