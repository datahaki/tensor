package ch.alpine.tensor.lie.ad;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class BchSeriesTest extends TestCase {
  private static final Distribution DISTRIBUTION = DiscreteUniformDistribution.of(-10, 11);

  public void test6() throws ClassNotFoundException, IOException {
    for (String name : new String[] { "so4", "so5", "sl3" }) {
      Tensor ad = ResourceData.object("/lie/" + name + "_ad.sparse");
      BchSeries6 bchSeries6 = Serialization.copy(new BchSeries6(ad));
      BakerCampbellHausdorff bakerCampbellHausdorff = new BakerCampbellHausdorff(ad, 6, Chop.NONE);
      int n = ad.length();
      Tensor x = RandomVariate.of(DISTRIBUTION, n);
      Tensor y = RandomVariate.of(DISTRIBUTION, n);
      assertEquals(bchSeries6.series(x, y), bakerCampbellHausdorff.series(x, y));
    }
  }

  public void test8() {
    for (String name : new String[] { "so4", "so5", "sl3" }) {
      Tensor ad = ResourceData.object("/lie/" + name + "_ad.sparse");
      BchSeries8 bchSeries8 = new BchSeries8(ad);
      BakerCampbellHausdorff bakerCampbellHausdorff = new BakerCampbellHausdorff(ad, 8, Chop.NONE);
      int n = ad.length();
      Tensor x = RandomVariate.of(DISTRIBUTION, n);
      Tensor y = RandomVariate.of(DISTRIBUTION, n);
      assertEquals(bchSeries8.series(x, y), bakerCampbellHausdorff.series(x, y));
    }
  }

  public void testQuad() {
    for (String name : new String[] { "so4", "so5", "sl3" }) {
      Tensor ad = ResourceData.object("/lie/" + name + "_ad.sparse");
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
}
