// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SquareMatrixQTest extends TestCase {
  public void testMatrix() {
    assertTrue(SquareMatrixQ.of(IdentityMatrix.of(10)));
    assertTrue(SquareMatrixQ.of(IdentityMatrix.of(10).unmodifiable()));
    assertFalse(SquareMatrixQ.of(Array.zeros(3, 4)));
  }

  public void testOthers() {
    assertFalse(SquareMatrixQ.of(UnitVector.of(10, 3)));
    assertFalse(SquareMatrixQ.of(LeviCivitaTensor.of(3)));
    assertFalse(SquareMatrixQ.of(RealScalar.ONE));
  }

  public void testEmpty() {
    assertFalse(SquareMatrixQ.of(Tensors.empty()));
  }

  public void testEmptyNested() {
    Tensor tensor = Tensors.fromString("{{}}");
    assertTrue(MatrixQ.of(tensor));
    assertFalse(SquareMatrixQ.of(tensor));
    assertTrue(SquareMatrixQ.of(Tensors.fromString("{{1}}")));
  }

  public void testRequire() {
    SquareMatrixQ.require(IdentityMatrix.of(10));
  }

  public void testNonArray() {
    assertFalse(SquareMatrixQ.of(Tensors.fromString("{{1, 2}, {{3}, 4}}")));
  }

  public void testRequireScalar() {
    AssertFail.of(() -> SquareMatrixQ.require(RealScalar.of(3)));
  }

  public void testRequireVector() {
    AssertFail.of(() -> SquareMatrixQ.require(Range.of(3, 10)));
  }

  public void testRequireMatrixNonSquare() {
    assertFalse(SquareMatrixQ.of(HilbertMatrix.of(3, 4)));
    AssertFail.of(() -> SquareMatrixQ.require(HilbertMatrix.of(3, 4)));
  }

  public void testRequireRank3() {
    assertFalse(SquareMatrixQ.of(LeviCivitaTensor.of(3)));
    AssertFail.of(() -> SquareMatrixQ.require(LeviCivitaTensor.of(3)));
  }
}
