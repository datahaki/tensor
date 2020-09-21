// code by jph
package ch.ethz.idsc.tensor.opt;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Roots;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.RandomQuaternion;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
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
    ExactTensorQ.require(coeffs);
    assertEquals(coeffs.toString(), "{75, -7}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots.toString(), "{75/7}");
    ScalarUnaryOperator series = Series.of(coeffs);
    assertEquals(series.apply(RealScalar.of(10)), RealScalar.of(5));
    assertEquals(series.apply(RealScalar.of(11)), RealScalar.of(-2));
  }

  public void testQuaternionDeg1() {
    Tensor xdata = Tensors.of(RandomQuaternion.get(), RandomQuaternion.get());
    Tensor ydata = Tensors.of(RandomQuaternion.get(), RandomQuaternion.get());
    Tensor coeffs = Fit.Polynomial.of(1, xdata, ydata);
    ExactTensorQ.require(coeffs);
    ScalarUnaryOperator series = Series.of(coeffs);
    for (int index = 0; index < xdata.length(); ++index)
      assertEquals(series.apply(xdata.Get(index)), ydata.Get(index));
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
