// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RandomQuaternion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.num.Roots;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FitTest extends TestCase {
  public void testDegree0() {
    Tensor ydata = Tensors.vector(5, -2);
    Tensor coeffs = Fit.polynomial_coeffs(Tensors.vector(10, 11), ydata, 0);
    assertEquals(coeffs.toString(), "{3/2}");
    assertEquals(Mean.of(ydata), RationalScalar.of(3, 2));
  }

  public void testDegree1() {
    Tensor coeffs = Fit.polynomial_coeffs(Tensors.vector(10, 11), Tensors.vector(5, -2), 1);
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
    Tensor coeffs = Fit.polynomial_coeffs(xdata, ydata, 1);
    ExactTensorQ.require(coeffs);
    ScalarUnaryOperator series = Polynomial.of(coeffs);
    for (int index = 0; index < xdata.length(); ++index)
      assertEquals(series.apply(xdata.Get(index)), ydata.Get(index));
  }

  public void testMixedUnits() {
    ScalarUnaryOperator pascal = QuantityMagnitude.SI().in("Pa");
    ScalarUnaryOperator kelvin = QuantityMagnitude.SI().in("K");
    for (int degree = 0; degree <= 4; ++degree) {
      Tensor x = Tensors.fromString("{100[K], 110.0[K], 120[K], 133[K], 140[K], 150[K]}");
      Tensor y = Tensors.fromString("{10[bar], 20[bar], 22[bar], 23[bar], 25[bar], 26.0[bar]}");
      {
        Tensor polynomial_coeffs = Fit.polynomial_coeffs(x, y, degree);
        Tensor derivative_coeffs = Polynomial.derivative_coeffs(polynomial_coeffs);
        assertEquals(polynomial_coeffs.length(), derivative_coeffs.length() + 1);
        // System.out.println(polynomial_coeffs);
        // System.out.println(derivative_coeffs);
      }
      ScalarUnaryOperator x_to_y = Fit.polynomial(x, y, degree);
      Scalar pressure = x_to_y.apply(Quantity.of(103, "K"));
      pascal.apply(pressure);
      ScalarUnaryOperator y_to_x = Fit.polynomial(y, x, degree);
      Scalar temperat = y_to_x.apply(Quantity.of(15, "bar"));
      kelvin.apply(temperat);
    }
  }

  public void testDegreeLargeFail() {
    AssertFail.of(() -> Fit.polynomial_coeffs(Tensors.vector(10, 11), Tensors.vector(5, -2), 2));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> Fit.polynomial_coeffs(Tensors.vector(10, 11), Tensors.vector(5, -2), -1));
  }
}
