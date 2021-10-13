// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RandomQuaternion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.num.Roots;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FitTest extends TestCase {
  public void testDegree0() {
    Tensor ydata = Tensors.vector(5, -2);
    Tensor coeffs = Fit.polynomial(0, Tensors.vector(10, 11), ydata);
    assertEquals(coeffs.toString(), "{3/2}");
    assertEquals(Mean.of(ydata), RationalScalar.of(3, 2));
  }

  public void testDegree1() {
    Tensor coeffs = Fit.polynomial(1, Tensors.vector(10, 11), Tensors.vector(5, -2));
    ExactTensorQ.require(coeffs);
    assertEquals(coeffs.toString(), "{75, -7}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots.toString(), "{75/7}");
    ScalarUnaryOperator series = Polynomial.of(coeffs);
    assertEquals(series.apply(RealScalar.of(10)), RealScalar.of(5));
    assertEquals(series.apply(RealScalar.of(11)), RealScalar.of(-2));
  }

  public void testQuaternionDeg1() {
    Tensor xdata = Tensors.of(RandomQuaternion.get(), RandomQuaternion.get());
    Tensor ydata = Tensors.of(RandomQuaternion.get(), RandomQuaternion.get());
    Tensor coeffs = Fit.polynomial(1, xdata, ydata);
    ExactTensorQ.require(coeffs);
    ScalarUnaryOperator series = Polynomial.of(coeffs);
    for (int index = 0; index < xdata.length(); ++index)
      assertEquals(series.apply(xdata.Get(index)), ydata.Get(index));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> Fit.polynomial(-1, Tensors.vector(10, 11), Tensors.vector(5, -2)));
  }
}
