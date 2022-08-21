// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Pi;

class CommonestFilterTest {
  @Test
  void testId() {
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor result = CommonestFilter.of(vector, 0);
    assertEquals(vector, result);
  }

  @Test
  void testMedian1() {
    Tensor vector = Tensors.vector(1, 2, 2, 0, 2, 2, 1);
    Tensor result = CommonestFilter.of(vector, 2);
    assertEquals(result, ConstantArray.of(RealScalar.TWO, 7));
  }

  @Test
  void testIdentityMatrix() {
    Tensor tensor = IdentityMatrix.of(10);
    for (int count = 2; count < 4; ++count) {
      Tensor result = CommonestFilter.of(tensor, count);
      assertEquals(result, ConstantArray.of(RealScalar.ZERO, 10, 10));
    }
  }

  @Test
  void testIdentityMatrixSparse() {
    Tensor tensor = IdentityMatrix.sparse(8);
    for (int count = 2; count < 4; ++count) {
      Tensor result = CommonestFilter.of(tensor, count);
      assertEquals(result, ConstantArray.of(RealScalar.ZERO, 8, 8));
    }
  }

  @Test
  void testFails() {
    assertThrows(Exception.class, () -> CommonestFilter.of(Pi.VALUE, 2));
    assertThrows(Exception.class, () -> CommonestFilter.of(HilbertMatrix.of(3), -1));
  }
}
