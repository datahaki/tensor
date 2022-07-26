// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.jet.DateTimeScalar;
import ch.alpine.tensor.lie.LehmerTensor;
import ch.alpine.tensor.mat.gr.IdempotentQ;
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
  void testSparse() {
    int n = 7;
    Tensor matrix = IdentityMatrix.sparse(n);
    assertInstanceOf(SparseArray.class, matrix);
    Tensor square = matrix.dot(matrix);
    assertInstanceOf(SparseArray.class, square);
    assertEquals(square, matrix);
    assertEquals(square, IdentityMatrix.of(n));
    IdempotentQ.of(matrix);
    Tensor matrow = matrix.get(1);
    assertInstanceOf(SparseArray.class, matrow);
    Tensor squrow = square.get(1);
    assertInstanceOf(SparseArray.class, squrow);
    // Tensor re = IdentityMatrix.of(n);
    // Timing timing1 = Timing.started();
    // re.dot(re);
    // timing1.stop();
    // System.out.println(timing0.seconds());
    // System.out.println(timing1.seconds());
  }

  @Test
  void testDateTimeScalar() {
    Scalar dts1 = DateTimeScalar.of(LocalDateTime.of(2000, 11, 3, 4, 5));
    Scalar dts2 = DateTimeScalar.of(LocalDateTime.of(2001, 10, 5, 4, 8));
    Scalar dts3 = DateTimeScalar.of(LocalDateTime.of(2002, 12, 7, 4, 11));
    Scalar dts4 = DateTimeScalar.of(LocalDateTime.of(2003, 11, 9, 4, 14));
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
