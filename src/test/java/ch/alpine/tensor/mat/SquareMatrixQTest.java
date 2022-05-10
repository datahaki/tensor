// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.lie.LeviCivitaTensor;

class SquareMatrixQTest {
  @Test
  public void testMatrix() {
    assertTrue(SquareMatrixQ.of(IdentityMatrix.of(10)));
    assertTrue(SquareMatrixQ.of(IdentityMatrix.of(10).unmodifiable()));
    assertFalse(SquareMatrixQ.of(Array.zeros(3, 4)));
  }

  @Test
  public void testOthers() {
    assertFalse(SquareMatrixQ.of(UnitVector.of(10, 3)));
    assertFalse(SquareMatrixQ.of(LeviCivitaTensor.of(3)));
    assertFalse(SquareMatrixQ.of(RealScalar.ONE));
  }

  @Test
  public void testEmpty() {
    assertFalse(SquareMatrixQ.of(Tensors.empty()));
  }

  @Test
  public void testEmptyNested() {
    Tensor tensor = Tensors.fromString("{{}}");
    assertTrue(MatrixQ.of(tensor));
    assertFalse(SquareMatrixQ.of(tensor));
    assertTrue(SquareMatrixQ.of(Tensors.fromString("{{1}}")));
  }

  @Test
  public void testRequire() {
    SquareMatrixQ.require(IdentityMatrix.of(10));
  }

  @Test
  public void testNonArray() {
    assertFalse(SquareMatrixQ.of(Tensors.fromString("{{1, 2}, {{3}, 4}}")));
  }

  @Test
  public void testRequireScalar() {
    assertThrows(TensorRuntimeException.class, () -> SquareMatrixQ.require(RealScalar.of(3)));
  }

  @Test
  public void testRequireVector() {
    assertThrows(TensorRuntimeException.class, () -> SquareMatrixQ.require(Range.of(3, 10)));
  }

  @Test
  public void testRequireMatrixNonSquare() {
    assertFalse(SquareMatrixQ.of(HilbertMatrix.of(3, 4)));
    assertThrows(TensorRuntimeException.class, () -> SquareMatrixQ.require(HilbertMatrix.of(3, 4)));
  }

  @Test
  public void testRequireRank3() {
    assertFalse(SquareMatrixQ.of(LeviCivitaTensor.of(3)));
    assertThrows(TensorRuntimeException.class, () -> SquareMatrixQ.require(LeviCivitaTensor.of(3)));
  }
}
