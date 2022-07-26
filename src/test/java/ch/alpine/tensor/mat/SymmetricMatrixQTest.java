// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.lie.Cross;
import ch.alpine.tensor.sca.Chop;

class SymmetricMatrixQTest {
  @Test
  void testHilbert() {
    SymmetricMatrixQ.require(HilbertMatrix.of(7));
    assertTrue(SymmetricMatrixQ.of(HilbertMatrix.of(10)));
  }

  @Test
  void testHilbert2() {
    assertFalse(SymmetricMatrixQ.of(HilbertMatrix.of(2, 3)));
  }

  @Test
  void testNumeric() {
    Tensor matrix = Tensors.fromString("{{1, 2.000000000000001}, {2, 1}}");
    SymmetricMatrixQ.require(matrix);
    assertFalse(SymmetricMatrixQ.of(matrix, Chop.NONE));
    assertThrows(Throw.class, () -> SymmetricMatrixQ.require(matrix, Chop.NONE));
  }

  @Test
  void testVector() {
    assertFalse(SymmetricMatrixQ.of(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testNaN() {
    assertFalse(SymmetricMatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  @Test
  void testScalar() {
    assertFalse(SymmetricMatrixQ.of(RealScalar.ONE));
  }

  @Test
  void testEmpty() {
    assertFalse(SymmetricMatrixQ.of(Tensors.empty()));
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> SymmetricMatrixQ.of(null));
  }

  @Test
  void testRequire() {
    SymmetricMatrixQ.require(IdentityMatrix.of(3));
    assertThrows(Throw.class, () -> SymmetricMatrixQ.require(Tensors.vector(1, 2, 3)));
    assertThrows(Throw.class, () -> SymmetricMatrixQ.require(Cross.skew3(Tensors.vector(1, 2, 3))));
  }

  @Test
  void testRequireEmptyFail() {
    assertThrows(Throw.class, () -> SymmetricMatrixQ.require(Tensors.empty()));
  }
}
