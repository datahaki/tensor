// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.spa.SparseArray;

class UnitVectorTest {
  @Test
  void testRegular() {
    assertEquals(UnitVector.of(10, 3), IdentityMatrix.of(10).get(3));
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(0, 0));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(-1, 0));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(3, -1));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(3, 4));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(10, 10));
  }

  @Test
  void testSparse() {
    Tensor vector = UnitVector.sparse(10, 3);
    assertInstanceOf(SparseArray.class, vector);
    assertEquals(vector, UnitVector.of(10, 3));
    assertEquals(vector, IdentityMatrix.of(10).get(3));
  }
}
