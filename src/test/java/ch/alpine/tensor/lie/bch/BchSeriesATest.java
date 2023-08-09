// code by jph
package ch.alpine.tensor.lie.bch;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.ExAd;

class BchSeriesATest {
  @Test
  void testOptimized() {
    Tensor ad = ExAd.SL2.ad();
    assertInstanceOf(BchSeriesA.class, BakerCampbellHausdorff.of(ad, 10));
  }
}
