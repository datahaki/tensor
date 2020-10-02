// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SymmetricMatrixQTest extends TestCase {
  public void testHilbert() {
    SymmetricMatrixQ.require(HilbertMatrix.of(7));
    assertTrue(SymmetricMatrixQ.of(HilbertMatrix.of(10)));
  }

  public void testHilbert2() {
    assertFalse(SymmetricMatrixQ.of(HilbertMatrix.of(2, 3)));
  }

  public void testNumeric() {
    Tensor matrix = Tensors.fromString("{{1, 2.000000000000001}, {2, 1}}");
    SymmetricMatrixQ.require(matrix);
    assertFalse(SymmetricMatrixQ.of(matrix, Chop.NONE));
    AssertFail.of(() -> SymmetricMatrixQ.require(matrix, Chop.NONE));
  }

  public void testVector() {
    assertFalse(SymmetricMatrixQ.of(Tensors.vector(1, 2, 3)));
  }

  public void testScalar() {
    assertFalse(SymmetricMatrixQ.of(RealScalar.ONE));
  }

  public void testEmpty() {
    assertFalse(SymmetricMatrixQ.of(Tensors.empty()));
  }

  public void testFailNull() {
    AssertFail.of(() -> SymmetricMatrixQ.of(null));
  }

  public void testRequire() {
    SymmetricMatrixQ.require(IdentityMatrix.of(3));
    AssertFail.of(() -> SymmetricMatrixQ.require(Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> SymmetricMatrixQ.require(Cross.skew3(Tensors.vector(1, 2, 3))));
  }

  public void testRequireEmptyFail() {
    AssertFail.of(() -> SymmetricMatrixQ.require(Tensors.empty()));
  }
}
