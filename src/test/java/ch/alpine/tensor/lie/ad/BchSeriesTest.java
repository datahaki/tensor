package ch.alpine.tensor.lie.ad;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class BchSeriesTest extends TestCase {
  private static final File DIR = HomeDirectory.file("Projects", "tensor", "src", "test", "resources", "lie");
  private static final Distribution DISTRIBUTION = DiscreteUniformDistribution.of(-10, 11);

  public void test06() throws ClassNotFoundException, IOException, DataFormatException {
    for (String name : new String[] { "so4", "so5", "sl3" }) {
      File file = new File(DIR, name + "_ad.sparse");
      Tensor ad = Import.object(file);
      BchSeries06 bchSeries06 = new BchSeries06(ad);
      BakerCampbellHausdorff bakerCampbellHausdorff = new BakerCampbellHausdorff(ad, 6, Chop.NONE);
      int n = ad.length();
      Tensor x = RandomVariate.of(DISTRIBUTION, n);
      Tensor y = RandomVariate.of(DISTRIBUTION, n);
      assertEquals(bchSeries06.series(x, y), bakerCampbellHausdorff.series(x, y));
    }
  }

  public void test08() throws ClassNotFoundException, IOException, DataFormatException {
    for (String name : new String[] { "so4", "so5", "sl3" }) {
      File file = new File(DIR, name + "_ad.sparse");
      Tensor ad = Import.object(file);
      BchSeries08 bchSeries08 = new BchSeries08(ad);
      BakerCampbellHausdorff bakerCampbellHausdorff = new BakerCampbellHausdorff(ad, 8, Chop.NONE);
      int n = ad.length();
      Tensor x = RandomVariate.of(DISTRIBUTION, n);
      Tensor y = RandomVariate.of(DISTRIBUTION, n);
      assertEquals(bchSeries08.series(x, y), bakerCampbellHausdorff.series(x, y));
    }
  }
}
