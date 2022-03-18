// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.RowReduce;

public class ToeplitzMatrixTest {
  @Test
  public void testSquare() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 4, 5));
    assertTrue(SquareMatrixQ.of(matrix));
    assertEquals(matrix.get(0), Range.of(3, 6));
    assertEquals(matrix.get(1), Range.of(2, 5));
    assertEquals(matrix.get(2), Range.of(1, 4));
  }

  @Test
  public void testSymmetric() {
    SymmetricMatrixQ.require(ToeplitzMatrix.of(Tensors.vector(5, 4, 3, 4, 5)));
  }

  @Test
  public void testRank2() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(0, 1, 0, 1, 0));
    SymmetricMatrixQ.require(matrix);
    assertEquals(RowReduce.of(matrix), Tensors.fromString("{{1, 0, 1}, {0, 1, 0}, {0, 0, 0}}"));
  }

  @Test
  public void testFullRank() {
    Tensor matrix = RowReduce.of(ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 5, 9)));
    assertEquals(matrix, IdentityMatrix.of(3));
  }

  @Test
  public void testRankDeficient() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 4, 5));
    assertEquals(Det.of(matrix), RealScalar.of(0));
    assertEquals(NullSpace.of(matrix), Tensors.fromString("{{1, -2, 1}}"));
  }

  @Test
  public void testFailEven() {
    assertThrows(TensorRuntimeException.class, () -> ToeplitzMatrix.of(Tensors.vector(1, 2)));
  }

  @Test
  public void testFailEmpty() {
    assertThrows(TensorRuntimeException.class, () -> ToeplitzMatrix.of(Tensors.empty()));
  }

  @Test
  public void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> ToeplitzMatrix.of(RealScalar.of(5)));
  }

  @Test
  public void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> ToeplitzMatrix.of(HilbertMatrix.of(5)));
  }
}
