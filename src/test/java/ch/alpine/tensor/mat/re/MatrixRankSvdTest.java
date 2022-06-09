// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

class MatrixRankSvdTest {
  @Test
  public void testZeros() {
    Tensor matrix = Array.zeros(9, 5);
    SingularValueDecomposition singularValueDecomposition = SingularValueDecomposition.of(matrix);
    int rank = MatrixRankSvd.of(singularValueDecomposition);
    assertEquals(rank, 0);
  }

  @Test
  public void testNumeric() {
    Tensor m = Tensors.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1e-40));
    assertEquals(MatrixRankSvd.of(m), 1);
    assertEquals(MatrixRank.of(m), 1); // <- numeric
  }

  @Test
  public void testNumeric2() {
    Tensor m = Transpose.of(Tensors.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1e-40)));
    assertEquals(MatrixRankSvd.of(m), 1);
    assertEquals(MatrixRank.of(m), 1); // <- numeric
  }
}
