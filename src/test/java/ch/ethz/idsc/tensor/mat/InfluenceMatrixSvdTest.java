// code by jph
package ch.ethz.idsc.tensor.mat;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class InfluenceMatrixSvdTest extends TestCase {
  public void testRankDeficient() {
    int n = 7;
    int _m = 5;
    Distribution distribution = NormalDistribution.standard();
    for (int r = 1; r < _m - 1; ++r) {
      Tensor m1 = RandomVariate.of(distribution, n, r);
      Tensor m2 = RandomVariate.of(distribution, r, _m);
      Tensor design = m1.dot(m2);
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      influenceMatrix.image(RandomVariate.of(distribution, n));
      Tensor matrix = influenceMatrix.matrix();
      Tensor zeros = Dot.of(influenceMatrix.residualMaker(), matrix);
      Tolerance.CHOP.requireAllZero(zeros);
    }
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(InfluenceMatrixSvd.class.getModifiers()));
  }
}
