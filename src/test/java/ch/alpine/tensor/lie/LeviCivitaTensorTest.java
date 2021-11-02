// code by jph
package ch.alpine.tensor.lie;

import java.util.Collections;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Numel;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LeviCivitaTensorTest extends TestCase {
  // former non-sparse implementation
  private static Tensor full(int d) {
    return Array.of(list -> Signature.of(Tensors.vector(list)), Collections.nCopies(d, d));
  }

  public void testRank0() {
    Tensor tensor = LeviCivitaTensor.of(0);
    assertEquals(tensor, RealScalar.ONE);
  }

  public void testRank1() {
    Tensor tensor = LeviCivitaTensor.of(1);
    assertEquals(tensor, Tensors.fromString("{1}"));
  }

  public void testRank2() {
    Tensor tensor = LeviCivitaTensor.of(2);
    assertEquals(tensor, Tensors.fromString("{{0, 1}, {-1, 0}}"));
  }

  public void testRank3() {
    Tensor tensor = LeviCivitaTensor.of(3);
    assertEquals(tensor, Tensors.fromString("{{{0, 0, 0}, {0, 0, 1}, {0, -1, 0}}, {{0, 0, -1}, {0, 0, 0}, {1, 0, 0}}, {{0, 1, 0}, {-1, 0, 0}, {0, 0, 0}}}"));
  }

  public void testAlternating() {
    for (int n = 0; n < 5; ++n) {
      Tensor tensor = LeviCivitaTensor.of(n);
      assertEquals(tensor, TensorWedge.of(tensor));
      assertEquals(Numel.of(tensor), Power.of(n, n).number().intValue());
      Tensor sparse = full(n);
      assertEquals(tensor, sparse);
    }
  }

  public void testRankNegativeFail() {
    AssertFail.of(() -> LeviCivitaTensor.of(-1));
  }
}
