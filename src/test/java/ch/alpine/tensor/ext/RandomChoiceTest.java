// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.IdentityMatrix;

class RandomChoiceTest {
  @Test
  void testSimple() {
    Set<Integer> set = new HashSet<>();
    for (int index = 0; index < 100; ++index) {
      int value = RandomChoice.of(Arrays.asList(1, 2, 3, 4));
      set.add(value);
    }
    assertEquals(set.size(), 4);
  }

  @Test
  void testTensor() {
    Scalar scalar = RandomChoice.of(Tensors.vector(2, 5));
    assertTrue(scalar.equals(RealScalar.of(2)) || scalar.equals(RealScalar.of(5)));
  }

  @Test
  void testIdentityMatrix() {
    Tensor tensor = RandomChoice.of(IdentityMatrix.of(3));
    assertTrue( //
        tensor.equals(UnitVector.of(3, 0)) || //
            tensor.equals(UnitVector.of(3, 1)) || //
            tensor.equals(UnitVector.of(3, 2)));
  }

  @Test
  void testTensorFail() {
    assertThrows(Exception.class, () -> RandomChoice.of(Tensors.vector()));
    assertThrows(Exception.class, () -> RandomChoice.of(RealScalar.ONE));
  }

  @Test
  void testListEmptyFail() {
    assertThrows(Exception.class, () -> RandomChoice.of(Collections.emptyList()));
  }
}
