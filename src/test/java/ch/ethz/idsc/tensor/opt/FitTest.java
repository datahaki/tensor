// code by jph
package ch.ethz.idsc.tensor.opt;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Roots;
import ch.ethz.idsc.tensor.red.Mean;
import junit.framework.TestCase;

public class FitTest extends TestCase {
  public void testDegree0() {
    Tensor ydata = Tensors.vector(5, -2);
    Tensor coeffs = Fit.Polynomial.of(0, Tensors.vector(10, 11), ydata);
    assertEquals(coeffs.toString(), "{3/2}");
    assertEquals(Mean.of(ydata), RationalScalar.of(3, 2));
  }

  public void testDegree1() {
    Tensor coeffs = Fit.Polynomial.of(1, Tensors.vector(10, 11), Tensors.vector(5, -2));
    assertEquals(coeffs.toString(), "{75, -7}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots.toString(), "{75/7}");
  }

  public void testNegativeFail() {
    try {
      Fit.Polynomial.of(-1, Tensors.vector(10, 11), Tensors.vector(5, -2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
