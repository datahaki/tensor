// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Numel;
import ch.alpine.tensor.sca.pow.Power;

class LeviCivitaTensorTest {
  // former non-sparse implementation
  private static Tensor full(int d) {
    return Array.of(list -> Signature.of(Tensors.vector(list)), Collections.nCopies(d, d));
  }

  @Test
  void testRank0() {
    Tensor tensor = LeviCivitaTensor.of(0);
    assertEquals(tensor, RealScalar.ONE);
  }

  @Test
  void testRank1() {
    Tensor tensor = LeviCivitaTensor.of(1);
    assertEquals(tensor, Tensors.fromString("{1}"));
  }

  @Test
  void testRank2() {
    Tensor tensor = LeviCivitaTensor.of(2);
    assertEquals(tensor, Tensors.fromString("{{0, 1}, {-1, 0}}"));
  }

  @Test
  void testRank3() {
    Tensor tensor = LeviCivitaTensor.of(3);
    assertEquals(tensor, Tensors.fromString("{{{0, 0, 0}, {0, 0, 1}, {0, -1, 0}}, {{0, 0, -1}, {0, 0, 0}, {1, 0, 0}}, {{0, 1, 0}, {-1, 0, 0}, {0, 0, 0}}}"));
  }

  @Test
  void testAlternating() {
    for (int n = 0; n < 5; ++n) {
      Tensor tensor = LeviCivitaTensor.of(n);
      assertEquals(tensor, TensorWedge.of(tensor));
      assertEquals(Numel.of(tensor), Power.of(n, n).number().intValue());
      Tensor sparse = full(n);
      assertEquals(tensor, sparse);
    }
  }

  @Test
  void testRankNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> LeviCivitaTensor.of(-1));
  }
}
