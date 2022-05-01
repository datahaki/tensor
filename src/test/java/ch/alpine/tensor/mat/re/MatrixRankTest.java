// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.N;

class MatrixRankTest {
  private static void _check(Tensor matrix, int expect) {
    {
      int rank = MatrixRank.of(matrix);
      assertEquals(rank, expect);
      assertEquals(rank, MatrixRank.of(Transpose.of(matrix)));
    }
    if (ExactTensorQ.of(matrix)) {
      Tensor numerc = matrix.map(N.DOUBLE);
      {
        int rank = MatrixRank.of(numerc);
        assertEquals(rank, expect);
        assertEquals(rank, MatrixRank.of(Transpose.of(numerc)));
      }
    }
  }

  @Test
  public void testRank() {
    _check(Array.zeros(3, 1), 0);
    _check(Tensors.of(Tensors.vector(0, 1, 0)), 1);
    _check(Tensors.of(Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 0)), 1);
    _check(Tensors.of(Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1)), 2);
    _check(Tensors.of(Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1)), 2);
  }

  @Test
  public void testNumeric3() {
    Tensor matrix = Tensors.fromString("{{0, 1.0, 0}, {0, 1, 1/1000000000000000000000000000000000000}}");
    assertEquals(MatrixRankSvd.of(matrix), 1);
    assertEquals(MatrixRank.of(matrix), 1); // <- numeric
  }

  @Test
  public void testExact() {
    Tensor matrix = Tensors.fromString("{{0, 1, 0}, {0, 1, 1/1000000000000000000000000000000000000}}");
    assertEquals(MatrixRankSvd.of(matrix), 1); // <- numeric
    assertEquals(MatrixRank.of(matrix), 2); // <- exact
  }

  @Test
  public void testSlim() {
    Tensor matrix = Tensors.of( //
        Array.zeros(3), //
        UnitVector.of(3, 1));
    _check(matrix, 1);
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> MatrixRank.of(RealScalar.TWO));
    assertThrows(TensorRuntimeException.class, () -> MatrixRank.of(Pi.VALUE));
  }

  @Test
  public void testVectorFail() {
    Tensor vector = Tensors.vector(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> MatrixRank.of(vector));
    assertThrows(NegativeArraySizeException.class, () -> MatrixRank.of(vector.map(N.DOUBLE)));
  }

  @Test
  public void testEmptyTensorFail() {
    Tensor tensor = Tensors.empty();
    assertThrows(NoSuchElementException.class, () -> MatrixRank.of(tensor));
  }
}
