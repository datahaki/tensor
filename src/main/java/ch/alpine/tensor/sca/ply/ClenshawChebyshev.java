// adapted from colt by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;

/** Evaluates the series of Chebyshev polynomials T_i at argument x.
 * The series is given by
 * y = sum_i=0...N-1 coeffs[i] T_i(x)
 * <p>
 * If coefficients are for the interval a to b, x must
 * have been transformed to x -> 2(2x - b - a)/(b-a) before
 * entering the routine. This maps x from (a, b) to (-1, 1),
 * over which the Chebyshev polynomials are defined.
 * <p>
 * If the coefficients are for the inverted interval, in
 * which (a, b) is mapped to (1/b, 1/a), the transformation
 * required is x -> 2(2ab/x - b - a)/(b-a). If b is infinity,
 * this becomes x -> 4a/x - 1.
 * <p>
 * Taking advantage of the recurrence properties of the
 * Chebyshev polynomials, the routine requires one more
 * addition per loop than evaluating a nested polynomial of
 * the same degree.
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Clenshaw_algorithm */
public class ClenshawChebyshev implements ScalarUnaryOperator {
  /** evaluation of affine combination of Chebyshev polynomials
   * defined over the interval [-1, 1] with coefficients
   * coeffs == {a, b, c, ...}
   * for the basis functions T0[x], T1[x], T2[x], ... that result
   * in the weighted combination as
   * p[x] == a T0[x] + b T1[x] + c T2[x] + ...
   * 
   * @param coeffs vector coefficients of the polynomial
   * @return */
  public static ScalarUnaryOperator of(Tensor coeffs) {
    return new ClenshawChebyshev(s -> s, coeffs);
  }

  public static ScalarUnaryOperator forward(Clip clip, Tensor coef) {
    Scalar den = clip.width().multiply(Rational.HALF);
    Scalar aff = clip.min().add(den);
    return of(x -> x.subtract(aff).divide(den), coef);
  }

  public static ScalarUnaryOperator inverse(Scalar num, Tensor coef) {
    return of(x -> num.divide(x).subtract(RealScalar.ONE), coef);
  }

  private static ScalarUnaryOperator of(ScalarUnaryOperator suo, Tensor _coef) {
    return new ClenshawChebyshev(suo, _coef);
  }

  // ---
  private final ScalarUnaryOperator suo;
  /** coeffs in reversed order */
  private final Scalar[] a;

  private ClenshawChebyshev(ScalarUnaryOperator suo, Tensor coeffs) {
    this.suo = suo;
    a = ScalarArray.ofVector(Reverse.of(coeffs));
  }

  @Override
  public Scalar apply(Scalar _x) {
    Scalar x1 = suo.apply(_x);
    Scalar x2 = x1.add(x1);
    int k = -1;
    Scalar bk0 = a[++k];
    Scalar bk1 = bk0.zero();
    Scalar bk2 = bk0.zero();
    while (k < a.length - 1) {
      bk2 = bk1;
      bk1 = bk0;
      bk0 = x2.multiply(bk1).subtract(bk2).add(a[++k]);
    }
    // TODO TENSOR non permanent
    Scalar r1 = bk0.subtract(bk2).add(a[k]).multiply(Rational.HALF);
    Scalar r2 = x1.multiply(bk1).subtract(bk2).add(a[k]);
    Chop._08.requireClose(r1, r2);
    return r1;
  }
}
