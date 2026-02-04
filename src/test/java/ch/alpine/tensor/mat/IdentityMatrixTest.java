// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.lie.LehmerTensor;
import ch.alpine.tensor.mat.gr.IdempotentMatrixQ;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.spa.SparseArray;

class IdentityMatrixTest {
  @Test
  void testOneQuantity() {
    Tensor matrix = DiagonalMatrix.of(2, Quantity.of(1, "s"));
    assertEquals(matrix, Tensors.fromString("{{1[s], 0[s]}, {0[s], 1[s]}}"));
  }

  @Test
  void testHilbertMatrix() {
    assertEquals(IdentityMatrix.of(HilbertMatrix.of(3)), IdentityMatrix.of(3));
  }

  @Test
  void testInplaceAdd() {
    Tensor matrix = HilbertMatrix.of(4);
    Tensor reference = matrix.add(IdentityMatrix.of(matrix));
    IdentityMatrix.inplaceAdd(matrix);
    assertEquals(matrix, reference);
  }

  @Test
  void testInplaceSub() {
    Tensor matrix = HilbertMatrix.of(5);
    Tensor reference = matrix.subtract(IdentityMatrix.of(matrix));
    IdentityMatrix.inplaceSub(matrix);
    assertEquals(matrix, reference);
  }

  @Test
  void testSparse() {
    int n = 7;
    Tensor matrix = IdentityMatrix.sparse(n);
    assertInstanceOf(SparseArray.class, matrix);
    Tensor square = matrix.dot(matrix);
    assertInstanceOf(SparseArray.class, square);
    assertEquals(square, matrix);
    assertEquals(square, IdentityMatrix.of(n));
    IdempotentMatrixQ.INSTANCE.isMember(matrix);
    Tensor matrow = matrix.get(1);
    assertInstanceOf(SparseArray.class, matrow);
    Tensor squrow = square.get(1);
    assertInstanceOf(SparseArray.class, squrow);
  }

  @Test
  void testDateTimeScalar() {
    Scalar dts1 = DateTime.of(2000, 11, 3, 4, 5);
    Scalar dts2 = DateTime.of(2001, 10, 5, 4, 8);
    Scalar dts3 = DateTime.of(2002, 12, 7, 4, 11);
    Scalar dts4 = DateTime.of(2003, 11, 9, 4, 14);
    Tensor matrix = Tensors.matrix(new Scalar[][] { { dts1, dts2 }, { dts3, dts4 } });
    Tensor eye = IdentityMatrix.of(matrix);
    assertThrows(Throw.class, () -> matrix.dot(eye));
  }

  @Test
  void testSparseFail() {
    assertThrows(IllegalArgumentException.class, () -> IdentityMatrix.sparse(0));
    assertThrows(IllegalArgumentException.class, () -> IdentityMatrix.sparse(-1));
  }

  @Test
  void testFailZero() {
    assertThrows(IllegalArgumentException.class, () -> IdentityMatrix.of(0));
  }

  @Test
  void testFailNegative() {
    assertThrows(IllegalArgumentException.class, () -> IdentityMatrix.of(-3));
  }

  @Test
  void testFailOneZero() {
    assertThrows(IllegalArgumentException.class, () -> DiagonalMatrix.of(0, Quantity.of(1, "s")));
  }

  @Test
  void testFailOneNegative() {
    assertThrows(IllegalArgumentException.class, () -> DiagonalMatrix.of(-3, Quantity.of(1, "s")));
  }

  @Test
  void testEmptyFail() {
    assertThrows(IllegalArgumentException.class, () -> IdentityMatrix.of(Tensors.empty()));
    assertThrows(IllegalArgumentException.class, () -> IdentityMatrix.of(Tensors.fromString("{{}}")));
    assertThrows(ClassCastException.class, () -> IdentityMatrix.of(LehmerTensor.of(3)));
  }
}
