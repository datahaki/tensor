// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Tan;

class PolynomialQTest {
  @Test
  void testSpline() {
    int length = 10;
    Tensor sample = RandomVariate.of(DiscreteUniformDistribution.of(-10, 30), length);
    int degree = 3;
    ScalarTensorFunction stf = BSplineFunctionString.of(degree, sample);
    ScalarUnaryOperator suo = s -> (Scalar) stf.apply(s);
    for (int c = 0; c < length - 1; ++c)
      assertTrue(PolynomialQ.of(suo, Clips.interval(c, c + 1), degree));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6 })
  void testSin(int degree) {
    assertFalse(PolynomialQ.of(Sin.FUNCTION, Clips.interval(0, 3), degree));
    assertFalse(PolynomialQ.of(Cos.FUNCTION, Clips.absolute(3), degree));
    assertFalse(PolynomialQ.of(Tan.FUNCTION, Clips.absolute(3), degree));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testPolynomial(int degree) {
    // FIXME does not perform well for degrees above 4
    Distribution distribution = TrapezoidalDistribution.with(0, 1, 2);
    Tensor coeffs = RandomVariate.of(distribution, degree).append(RealScalar.ONE);
    Polynomial polynomial = Polynomial.of(coeffs);
    assertTrue(PolynomialQ.of(polynomial, Clips.absoluteOne(), degree));
  }
}
