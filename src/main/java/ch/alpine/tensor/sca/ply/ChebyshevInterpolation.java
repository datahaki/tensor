// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.red.Total;

/** References:
 * "Barycentric Lagrange Interpolation"
 * by Jean-Paul Berrut, Lloyd N. Trefethen
 * 
 * "Barycentric interpolation in Chebyshev points"
 * Trefethen 5.2 eq. 5.13 */
public class ChebyshevInterpolation implements ScalarUnaryOperator {
  public static ScalarUnaryOperator of(ScalarUnaryOperator function, ChebyshevNodes chebyshevNodes, int n) {
    return new ChebyshevInterpolation(function, chebyshevNodes, n);
  }

  // TODO TENSOR MAT remove when methods coincide
  public static ScalarUnaryOperator alt(ScalarUnaryOperator function, ChebyshevNodes chebyshevNodes, int n) {
    Tensor coeffs = LinearSolve.of(chebyshevNodes.matrix(n), chebyshevNodes.of(n).map(function));
    return ClenshawChebyshev.of(coeffs);
  }

  // private final BinaryAverage binaryAverage;
  private final Tensor xv;
  private final Tensor f;

  private ChebyshevInterpolation(ScalarUnaryOperator function, ChebyshevNodes chebyshevNodes, int n) {
    // this.binaryAverage=binaryAverage;
    xv = chebyshevNodes.of(n);
    f = xv.map(function);
  }

  @Override
  public Scalar apply(Scalar x) {
    Tensor weights = Array.zeros(xv.length());
    for (int j = 0; j < xv.length(); ++j) {
      Scalar d = x.subtract(xv.Get(j));
      if (Tolerance.CHOP.isZero(d))
        return f.Get(j);
      Scalar w = RealScalar.of((j % 2) == 0 ? 1 : -1).divide(d);
      weights.set(w, j);
    }
    weights.set(RationalScalar.HALF::multiply, 0);
    weights.set(RationalScalar.HALF::multiply, weights.length() - 1);
    return (Scalar) weights.divide(Total.ofVector(weights)).dot(f);
  }
}
