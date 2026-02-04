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
import ch.alpine.tensor.lie.rot.Cross;
import ch.alpine.tensor.sca.Chop;

class SymmetricMatrixQTest {
  @Test
  void testHilbert() {
    SymmetricMatrixQ.INSTANCE.requireMember(HilbertMatrix.of(7));
    assertTrue(SymmetricMatrixQ.INSTANCE.isMember(HilbertMatrix.of(10)));
  }

  @Test
  void testHilbert2() {
    assertFalse(SymmetricMatrixQ.INSTANCE.isMember(HilbertMatrix.of(2, 3)));
  }

  @Test
  void testNumeric() {
    Tensor matrix = Tensors.fromString("{{1, 2.000000000000001}, {2, 1}}");
    SymmetricMatrixQ.INSTANCE.requireMember(matrix);
    assertFalse(new SymmetricMatrixQ(Chop.NONE).isMember(matrix));
    assertThrows(Throw.class, () -> new SymmetricMatrixQ(Chop.NONE).requireMember(matrix));
  }

  @Test
  void testVector() {
    assertFalse(SymmetricMatrixQ.INSTANCE.isMember(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testNaN() {
    assertFalse(SymmetricMatrixQ.INSTANCE.isMember(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  @Test
  void testScalar() {
    assertFalse(SymmetricMatrixQ.INSTANCE.isMember(RealScalar.ONE));
  }

  @Test
  void testEmpty() {
    assertFalse(SymmetricMatrixQ.INSTANCE.isMember(Tensors.empty()));
  }

  @Test
  void testFailNull() {
    assertThrows(NullPointerException.class, () -> SymmetricMatrixQ.INSTANCE.isMember(null));
  }

  @Test
  void testRequire() {
    SymmetricMatrixQ.INSTANCE.requireMember(IdentityMatrix.of(3));
    assertThrows(Throw.class, () -> SymmetricMatrixQ.INSTANCE.requireMember(Tensors.vector(1, 2, 3)));
    assertThrows(Throw.class, () -> SymmetricMatrixQ.INSTANCE.requireMember(Cross.skew3(Tensors.vector(1, 2, 3))));
  }

  @Test
  void testRequireEmptyFail() {
    assertThrows(Throw.class, () -> SymmetricMatrixQ.INSTANCE.requireMember(Tensors.empty()));
  }
}
