// code by jph
package ch.alpine.tensor.lie.bch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.ExAd;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;

class BchSeriesTest {
  private static final Distribution DISTRIBUTION = DiscreteUniformDistribution.of(-100, 101);

  @ParameterizedTest
  @EnumSource
  void test6(ExAd exAd) throws ClassNotFoundException, IOException {
    Tensor ad = exAd.ad();
    BchSeries6 bchSeries6 = Serialization.copy(new BchSeries6(ad));
    BakerCampbellHausdorff bakerCampbellHausdorff = new BakerCampbellHausdorff(ad, 6, Chop.NONE);
    int n = ad.length();
    Tensor x = RandomVariate.of(DISTRIBUTION, n);
    Tensor y = RandomVariate.of(DISTRIBUTION, n);
    assertEquals(bchSeries6.series(x, y), bakerCampbellHausdorff.series(x, y));
  }

  @ParameterizedTest
  @EnumSource
  void test8(ExAd exAd) {
    Tensor ad = exAd.ad();
    BchSeries8 bchSeries8 = new BchSeries8(ad);
    BakerCampbellHausdorff bakerCampbellHausdorff = new BakerCampbellHausdorff(ad, 8, Chop.NONE);
    int n = ad.length();
    Tensor x = RandomVariate.of(DISTRIBUTION, n);
    Tensor y = RandomVariate.of(DISTRIBUTION, n);
    assertEquals(bchSeries8.series(x, y), bakerCampbellHausdorff.series(x, y));
  }

  @ParameterizedTest
  @EnumSource
  void testQuad(ExAd exAd) {
    Tensor ad = exAd.ad();
    int n = ad.length();
    Tensor x = RandomVariate.of(DISTRIBUTION, n);
    Tensor y = RandomVariate.of(DISTRIBUTION, n);
    Tensor adx = ad.dot(x);
    Tensor ady = ad.dot(y);
    Tensor r1 = Dot.of(adx, ady, adx, y);
    Tensor r2 = Dot.of(ady, adx, adx, y);
    assertEquals(r1, r2);
  }
}
