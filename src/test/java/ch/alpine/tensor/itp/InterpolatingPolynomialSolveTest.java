// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import junit.framework.TestCase;

public class InterpolatingPolynomialSolveTest extends TestCase {
  private static Tensor polynomial_coeffs(Tensor xdata, Tensor ydata, int degree) {
    return LinearSolve.of(VandermondeMatrix.of(xdata), ydata);
  }

  public void testDegree1() {
    Tensor x = Tensors.vector(10, 11);
    Tensor y = Tensors.vector(5, -2);
    Tensor coeffs = polynomial_coeffs(x, y, 1);
    ExactTensorQ.require(coeffs);
    Tensor coeff2 = InterpolatingPolynomialSolve.of(x, y);
    ExactTensorQ.require(coeff2);
    assertEquals(coeffs, coeff2);
  }

  public void testDegree2() {
    Tensor x = Tensors.vector(10, 11, 14);
    Tensor y = Tensors.vector(5, -2, 1);
    Tensor coeffs = polynomial_coeffs(x, y, 2);
    ExactTensorQ.require(coeffs);
    Tensor coeff2 = InterpolatingPolynomialSolve.of(x, y);
    ExactTensorQ.require(coeff2);
    assertEquals(coeffs, coeff2);
  }

  public void testDegreesUnits() {
    Tensor xdata = Tensors.vector(10, 11, 14, 20).map(s -> Quantity.of(s, "K"));
    Tensor ydata = Tensors.vector(5, -2, 1, 9).map(s -> Quantity.of(s, "bar"));
    for (int degree = 0; degree <= 3; ++degree) {
      Tensor x = xdata.extract(0, degree + 1);
      Tensor y = ydata.extract(0, degree + 1);
      Tensor coeffs = polynomial_coeffs(x, y, degree);
      ExactTensorQ.require(coeffs);
      Tensor coeff2 = InterpolatingPolynomialSolve.of(x, y);
      ExactTensorQ.require(coeff2);
      assertEquals(coeffs, coeff2);
    }
  }

  public void testDegreesUnitsNumeric() {
    Tensor xdata = Tensors.vector(10, 11, 14, 20).map(s -> Quantity.of(s, "K")).map(N.DOUBLE);
    Tensor ydata = Tensors.vector(5, -2, 1, 9).map(s -> Quantity.of(s, "bar")).map(N.DOUBLE);
    for (int degree = 0; degree <= 3; ++degree) {
      Tensor x = xdata.extract(0, degree + 1);
      Tensor y = ydata.extract(0, degree + 1);
      Tensor coeffs = polynomial_coeffs(x, y, degree);
      Tensor coeff2 = InterpolatingPolynomialSolve.of(x, y);
      Chop._08.requireClose(coeffs, coeff2);
    }
  }
}
