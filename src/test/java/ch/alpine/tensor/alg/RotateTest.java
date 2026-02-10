// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;

class RotateTest {
  @Test
  void testVector() {
    Tensor vector = Tensors.vector(0, 1, 2, 3, 4).unmodifiable();
    assertEquals(Rotate.PULL.of(vector, -6), Tensors.vector(4, 0, 1, 2, 3));
    assertEquals(Rotate.PULL.of(vector, -1), Tensors.vector(4, 0, 1, 2, 3));
    assertEquals(Rotate.PULL.of(vector, +0), Tensors.vector(0, 1, 2, 3, 4));
    assertEquals(Rotate.PULL.of(vector, +1), Tensors.vector(1, 2, 3, 4, 0));
    assertEquals(Rotate.PULL.of(vector, +2), Tensors.vector(2, 3, 4, 0, 1));
    assertEquals(Rotate.PULL.of(vector, +7), Tensors.vector(2, 3, 4, 0, 1));
    assertEquals(vector, Range.of(0, 5));
  }

  @Test
  void testReferences() {
    Tensor matrix = HilbertMatrix.of(3);
    Tensor tensor = Rotate.PULL.of(matrix, 1);
    matrix.set(_ -> RealScalar.ONE, Tensor.ALL, 1);
    assertEquals(tensor, Rotate.PULL.of(HilbertMatrix.of(3), 1));
  }

  @Test
  void testVector2() {
    Tensor vector = Tensors.vector(0, 1, 2, 3, 4).unmodifiable();
    assertEquals(Rotate.PUSH.of(vector, +6), Tensors.vector(4, 0, 1, 2, 3));
    assertEquals(Rotate.PUSH.of(vector, +1), Tensors.vector(4, 0, 1, 2, 3));
    assertEquals(Rotate.PUSH.of(vector, +0), Tensors.vector(0, 1, 2, 3, 4));
    assertEquals(Rotate.PUSH.of(vector, -1), Tensors.vector(1, 2, 3, 4, 0));
    assertEquals(Rotate.PUSH.of(vector, -2), Tensors.vector(2, 3, 4, 0, 1));
    assertEquals(Rotate.PUSH.of(vector, -7), Tensors.vector(2, 3, 4, 0, 1));
    assertEquals(vector, Range.of(0, 5));
  }

  @Test
  void testMatrix2() {
    int size = 5;
    for (int k = 0; k < size * 2; ++k) {
      Tensor matrix = Rotate.PUSH.of(IdentityMatrix.of(size), -k);
      assertEquals(matrix.get(0), UnitVector.of(size, k % size));
    }
  }

  @ParameterizedTest
  @EnumSource
  void testEmpty(Rotate rotate) {
    assertEquals(rotate.of(Tensors.empty(), +1), Tensors.empty());
    assertEquals(rotate.of(Tensors.empty(), +0), Tensors.empty());
    assertEquals(rotate.of(Tensors.empty(), -1), Tensors.empty());
  }

  @ParameterizedTest
  @EnumSource
  void testFailScalar(Rotate rotate) {
    assertThrows(Throw.class, () -> rotate.of(RealScalar.ONE, 0));
  }

  @ParameterizedTest
  @EnumSource
  void testFailNull(Rotate rotate) {
    assertThrows(NullPointerException.class, () -> rotate.of(null, 0));
  }
}
