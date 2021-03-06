// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.lie.Cross;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
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

  public void testNaN() {
    assertFalse(SymmetricMatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
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
