// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.NullSpace;
import ch.ethz.idsc.tensor.mat.RowReduce;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ToeplitzMatrixTest extends TestCase {
  public void testSquare() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 4, 5));
    assertTrue(SquareMatrixQ.of(matrix));
    assertEquals(matrix.get(0), Range.of(3, 6));
    assertEquals(matrix.get(1), Range.of(2, 5));
    assertEquals(matrix.get(2), Range.of(1, 4));
  }

  public void testSymmetric() {
    SymmetricMatrixQ.require(ToeplitzMatrix.of(Tensors.vector(5, 4, 3, 4, 5)));
  }

  public void testRank2() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(0, 1, 0, 1, 0));
    SymmetricMatrixQ.require(matrix);
    assertEquals(RowReduce.of(matrix), Tensors.fromString("{{1, 0, 1}, {0, 1, 0}, {0, 0, 0}}"));
  }

  public void testFullRank() {
    Tensor matrix = RowReduce.of(ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 5, 9)));
    assertEquals(matrix, IdentityMatrix.of(3));
  }

  public void testRankDeficient() {
    Tensor matrix = ToeplitzMatrix.of(Tensors.vector(1, 2, 3, 4, 5));
    assertEquals(Det.of(matrix), RealScalar.of(0));
    assertEquals(NullSpace.of(matrix), Tensors.fromString("{{1, -2, 1}}"));
  }

  public void testFailEven() {
    AssertFail.of(() -> ToeplitzMatrix.of(Tensors.vector(1, 2)));
  }

  public void testFailEmpty() {
    AssertFail.of(() -> ToeplitzMatrix.of(Tensors.empty()));
  }

  public void testFailScalar() {
    AssertFail.of(() -> ToeplitzMatrix.of(RealScalar.of(5)));
  }

  public void testFailMatrix() {
    AssertFail.of(() -> ToeplitzMatrix.of(HilbertMatrix.of(5)));
  }
}
