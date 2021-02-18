// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RootsDegree1Test extends TestCase {
  public void testGaussScalar() {
    Tensor coeffs = Tensors.of(GaussScalar.of(4, 7), GaussScalar.of(5, 7));
    Tensor roots = Roots.of(coeffs);
    Tensor zeros = roots.map(Series.of(coeffs));
    assertEquals(zeros, Tensors.of(GaussScalar.of(0, 7)));
    Chop.NONE.requireAllZero(zeros);
    Tolerance.CHOP.requireAllZero(zeros);
    assertEquals(roots, Tensors.of(GaussScalar.of(2, 7)));
  }
}
