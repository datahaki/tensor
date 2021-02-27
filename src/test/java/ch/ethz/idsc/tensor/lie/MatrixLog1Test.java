// code by jph
package ch.ethz.idsc.tensor.lie;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MatrixLog1Test extends TestCase {
  public void test1x1() {
    for (int count = 0; count < 10; ++count) {
      Tensor x = RandomVariate.of(UniformDistribution.of(-10, 6), 1, 1);
      Tensor exp = MatrixExp.of(x);
      Tensor log = MatrixLog._of(exp);
      Tensor cmp = MatrixLog1.of(exp);
      Chop._04.requireClose(log, cmp);
    }
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(MatrixLog1.class.getModifiers()));
  }
}
