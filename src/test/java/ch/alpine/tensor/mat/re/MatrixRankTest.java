// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
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
  void testRank() {
    _check(Array.zeros(3, 1), 0);
    _check(Tensors.of(Tensors.vector(0, 1, 0)), 1);
    _check(Tensors.of(Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 0)), 1);
    _check(Tensors.of(Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1)), 2);
    _check(Tensors.of(Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1)), 2);
  }

  @Test
  void testNumeric3() {
    Tensor matrix = Tensors.fromString("{{0, 1.0, 0}, {0, 1, 1/1000000000000000000000000000000000000}}");
    assertEquals(MatrixRank.of(matrix), 1); // <- numeric
  }

  @Test
  void testExact() {
    Tensor matrix = Tensors.fromString("{{0, 1, 0}, {0, 1, 1/1000000000000000000000000000000000000}}");
    assertEquals(MatrixRank.of(matrix), 2); // <- exact
  }

  @Test
  void testSlim() {
    Tensor matrix = Tensors.of( //
        Array.zeros(3), //
        UnitVector.of(3, 1));
    _check(matrix, 1);
  }

  @Test
  void testZeros() {
    Tensor matrix = Array.zeros(9, 5);
    int rank = MatrixRank.of(matrix);
    assertEquals(rank, 0);
  }

  @Test
  void testNumeric() {
    Tensor m = Tensors.of( //
        Tensors.vector(0, 1, 0), //
        Tensors.vector(0, 1, 1e-40));
    assertEquals(MatrixRank.of(m), 1); // <- numeric
  }

  @Test
  void testNumeric2() {
    Tensor m = Transpose.of(Tensors.of( //
        Tensors.vector(0, 1, 0), //
        Tensors.vector(0, 1, 1e-40)));
    assertEquals(MatrixRank.of(m), 1); // <- numeric
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> MatrixRank.of(RealScalar.TWO));
    assertThrows(Throw.class, () -> MatrixRank.of(Pi.VALUE));
  }

  @Test
  void testVectorFail() {
    Tensor vector = Tensors.vector(1, 2, 3);
    assertThrows(IllegalArgumentException.class, () -> MatrixRank.of(vector));
    assertThrows(NegativeArraySizeException.class, () -> MatrixRank.of(vector.map(N.DOUBLE)));
  }

  @Test
  void testEmptyTensorFail() {
    Tensor tensor = Tensors.empty();
    assertThrows(NoSuchElementException.class, () -> MatrixRank.of(tensor));
  }
}
