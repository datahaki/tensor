// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DenmanBeaversTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    int fails = 0;
    for (int count = 0; count < 100; ++count)
      try {
        Tensor a = RandomVariate.of(distribution, 2, 2);
        DenmanBeavers denmanBeavers = new DenmanBeavers(a);
        Tensor as = denmanBeavers.y0.dot(denmanBeavers.y0);
        Chop._10.requireClose(a, as);
        Tensor e = denmanBeavers.y0.dot(denmanBeavers.z0);
        Chop._10.requireClose(e, IdentityMatrix.of(2));
      } catch (Exception exception) {
        ++fails;
      }
    assertTrue(fails < 90);
  }
}
