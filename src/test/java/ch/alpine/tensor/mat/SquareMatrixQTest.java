// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.lie.LeviCivitaTensor;

class SquareMatrixQTest {
  @Test
  void testMatrix() {
    assertTrue(SquareMatrixQ.INSTANCE.isMember(IdentityMatrix.of(10)));
    assertTrue(SquareMatrixQ.INSTANCE.isMember(IdentityMatrix.of(10).unmodifiable()));
    assertFalse(SquareMatrixQ.INSTANCE.isMember(Array.zeros(3, 4)));
  }

  @Test
  void testOthers() {
    assertFalse(SquareMatrixQ.INSTANCE.isMember(UnitVector.of(10, 3)));
    assertFalse(SquareMatrixQ.INSTANCE.isMember(LeviCivitaTensor.of(3)));
    assertFalse(SquareMatrixQ.INSTANCE.isMember(RealScalar.ONE));
  }

  @Test
  void testEmpty() {
    assertFalse(SquareMatrixQ.INSTANCE.isMember(Tensors.empty()));
  }

  @Test
  void testEmptyNested() {
    Tensor tensor = Tensors.fromString("{{}}");
    assertTrue(MatrixQ.of(tensor));
    assertFalse(SquareMatrixQ.INSTANCE.isMember(tensor));
    assertTrue(SquareMatrixQ.INSTANCE.isMember(Tensors.fromString("{{1}}")));
  }

  @Test
  void testRequire() {
    SquareMatrixQ.INSTANCE.requireMember(IdentityMatrix.of(10));
  }

  @Test
  void testNonArray() {
    assertFalse(SquareMatrixQ.INSTANCE.isMember(Tensors.fromString("{{1, 2}, {{3}, 4}}")));
  }

  @Test
  void testRequireScalar() {
    assertThrows(Throw.class, () -> SquareMatrixQ.INSTANCE.requireMember(RealScalar.of(3)));
  }

  @Test
  void testRequireVector() {
    assertThrows(Throw.class, () -> SquareMatrixQ.INSTANCE.requireMember(Range.of(3, 10)));
  }

  @Test
  void testRequireMatrixNonSquare() {
    assertFalse(SquareMatrixQ.INSTANCE.isMember(HilbertMatrix.of(3, 4)));
    assertThrows(Throw.class, () -> SquareMatrixQ.INSTANCE.requireMember(HilbertMatrix.of(3, 4)));
  }

  @Test
  void testRequireRank3() {
    assertFalse(SquareMatrixQ.INSTANCE.isMember(LeviCivitaTensor.of(3)));
    assertThrows(Throw.class, () -> SquareMatrixQ.INSTANCE.requireMember(LeviCivitaTensor.of(3)));
  }
}
