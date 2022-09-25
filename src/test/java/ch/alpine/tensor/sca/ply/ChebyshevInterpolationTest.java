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
  // @RepeatedTest(1)
  // void testSimple(RepetitionInfo repetitionInfo) {
  // ScalarUnaryOperator suo = Exp.FUNCTION;
  // int n = 3 + repetitionInfo.getCurrentRepetition();
  // // ScalarUnaryOperator apx1 = ChebyshevInterpolation.of(suo, chebyshevNodes, n);
  // ChebyshevNodes chebyshevNodes = ChebyshevNodes._1;
  // Tensor f = chebyshevNodes.of(n).map(suo);
  // Tensor coeffs1 = LinearSolve.of(chebyshevNodes.matrix(n), f);
  // Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
  // // .divide(scalar);
  // Tensor coeffs2 = FourierDCT._3.of(f.multiply(scalar));
  // // Tensor matr = FourierDCTMatrix._2.of(8);
  // // System.out.println(Pretty.of(matr.subtract(Transpose.of(matr)).map(Round._3)));
  // // Tolerance.CHOP.requireClose(p1, p2);
  // // System.out.println(coeffs1);
  // // System.out.println(coeffs2);
  // }
}
