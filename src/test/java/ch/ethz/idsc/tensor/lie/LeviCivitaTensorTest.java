// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LeviCivitaTensorTest extends TestCase {
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

  public void testRankNegativeFail() {
    try {
      LeviCivitaTensor.of(-1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
