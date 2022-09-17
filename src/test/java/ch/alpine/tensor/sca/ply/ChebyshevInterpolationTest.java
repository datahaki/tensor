// code by jph
package ch.alpine.tensor.sca.ply;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.exp.Exp;

class ChebyshevInterpolationTest {
  @ParameterizedTest
  @EnumSource
  void test(ChebyshevNodes chebyshevNodes) {
    ScalarUnaryOperator suo = Exp.FUNCTION;
    int n = 13;
    // ScalarUnaryOperator apx1 = ChebyshevInterpolation.of(suo, chebyshevNodes, n);
    ScalarUnaryOperator apx2 = ChebyshevInterpolation.alt(suo, chebyshevNodes, n);
    Tensor knots = chebyshevNodes.of(n);
    InterpolatingPolynomial ip = InterpolatingPolynomial.of(knots);
    ScalarUnaryOperator suo3 = ip.scalarUnaryOperator(knots.map(suo));
    Tensor domain = Subdivide.of(-1, 1, 10);
    Tensor p1 = domain.map(suo3);
    Tensor p2 = domain.map(apx2);
    Tolerance.CHOP.requireClose(p1, p2);
  }
}
