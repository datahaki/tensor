// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Fold;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.usr.AssertFail;
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

  public void testDet() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      assertEquals(Fold.of((t, v) -> v.dot(t), LeviCivitaTensor.of(n), matrix), Det.of(matrix));
    }
  }

  public void testAlternating() {
    for (int n = 0; n < 5; ++n) {
      Tensor tensor = LeviCivitaTensor.of(n);
      assertEquals(tensor, TensorWedge.of(tensor));
    }
  }

  public void testRankNegativeFail() {
    AssertFail.of(() -> LeviCivitaTensor.of(-1));
  }
}
