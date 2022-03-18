// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.jet.DateTimeScalar;
import ch.alpine.tensor.jet.DurationScalar;
import ch.alpine.tensor.lie.LehmerTensor;
import ch.alpine.tensor.mat.gr.IdempotentQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.spa.SparseArray;
import ch.alpine.tensor.usr.AssertFail;

public class IdentityMatrixTest {
  @Test
  public void testOneQuantity() {
    Tensor matrix = DiagonalMatrix.of(2, Quantity.of(1, "s"));
    assertEquals(matrix, Tensors.fromString("{{1[s], 0[s]}, {0[s], 1[s]}}"));
  }

  @Test
  public void testHilbertMatrix() {
    assertEquals(IdentityMatrix.of(HilbertMatrix.of(3)), IdentityMatrix.of(3));
  }

  @Test
  public void testSparse() {
    int n = 7;
    Tensor matrix = IdentityMatrix.sparse(n);
    assertTrue(matrix instanceof SparseArray);
    Timing timing0 = Timing.started();
    Tensor square = matrix.dot(matrix);
    timing0.stop();
    assertTrue(square instanceof SparseArray);
    assertEquals(square, matrix);
    assertEquals(square, IdentityMatrix.of(n));
    IdempotentQ.of(matrix);
    Tensor matrow = matrix.get(1);
    assertTrue(matrow instanceof SparseArray);
    Tensor squrow = square.get(1);
    assertTrue(squrow instanceof SparseArray);
    // Tensor re = IdentityMatrix.of(n);
    // Timing timing1 = Timing.started();
    // re.dot(re);
    // timing1.stop();
    // System.out.println(timing0.seconds());
    // System.out.println(timing1.seconds());
  }

  @Test
  public void testDurationScalar() {
    DurationScalar ds1 = DurationScalar.fromSeconds(RealScalar.of(3));
    DurationScalar ds2 = DurationScalar.fromSeconds(RealScalar.of(3.123));
    DurationScalar ds3 = DurationScalar.fromSeconds(RealScalar.of(10));
    DurationScalar ds4 = DurationScalar.fromSeconds(RealScalar.of(9));
    Tensor matrix = Tensors.matrix(new Scalar[][] { { ds1, ds2 }, { ds3, ds4 } });
    Tensor eye = IdentityMatrix.of(matrix);
    assertEquals(matrix.dot(eye), matrix);
    assertEquals(eye.dot(matrix), matrix);
  }

  @Test
  public void testDateTimeScalar() {
    Scalar dts1 = DateTimeScalar.of(LocalDateTime.of(2000, 11, 3, 4, 5));
    Scalar dts2 = DateTimeScalar.of(LocalDateTime.of(2001, 10, 5, 4, 8));
    Scalar dts3 = DateTimeScalar.of(LocalDateTime.of(2002, 12, 7, 4, 11));
    Scalar dts4 = DateTimeScalar.of(LocalDateTime.of(2003, 11, 9, 4, 14));
    Tensor matrix = Tensors.matrix(new Scalar[][] { { dts1, dts2 }, { dts3, dts4 } });
    Tensor eye = IdentityMatrix.of(matrix);
    AssertFail.of(() -> matrix.dot(eye));
  }

  @Test
  public void testSparseFail() {
    AssertFail.of(() -> IdentityMatrix.sparse(0));
    AssertFail.of(() -> IdentityMatrix.sparse(-1));
  }

  @Test
  public void testFailZero() {
    AssertFail.of(() -> IdentityMatrix.of(0));
  }

  @Test
  public void testFailNegative() {
    AssertFail.of(() -> IdentityMatrix.of(-3));
  }

  @Test
  public void testFailOneZero() {
    AssertFail.of(() -> DiagonalMatrix.of(0, Quantity.of(1, "s")));
  }

  @Test
  public void testFailOneNegative() {
    AssertFail.of(() -> DiagonalMatrix.of(-3, Quantity.of(1, "s")));
  }

  @Test
  public void testEmptyFail() {
    AssertFail.of(() -> IdentityMatrix.of(Tensors.empty()));
    AssertFail.of(() -> IdentityMatrix.of(Tensors.fromString("{{}}")));
    AssertFail.of(() -> IdentityMatrix.of(LehmerTensor.of(3)));
  }
}
