// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.sca.Clip;

/** Evaluates the series of Chebyshev polynomials Ti at argument x/2.
 * The series is given by
 * <pre>
 * N-1
 * - '
 * y = > coef[i] T (x/2)
 * - i
 * i=0
 * </pre>
 * Coefficients are stored in reverse order, i.e. the zero
 * order term is last in the array. Note N is the number of
 * coefficients, not the order.
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
 * SPEED:
 * <p>
 * Taking advantage of the recurrence properties of the
 * Chebyshev polynomials, the routine requires one more
 * addition per loop than evaluating a nested polynomial of
 * the same degree.
 * 
 * @param coef the coefficients of the polynomial
 * @param x argument to the polynomial
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Clenshaw_algorithm */
public class ChebyshevClenshaw implements ScalarUnaryOperator {
  /** evaluation of affine combination of Chebyshev polynomials
   * defined over interval [-1, 1] with coefficients
   * coeffs == {a, b, c, ...}
   * that define the weights of T0[x], T1[x], T2[x], ... as
   * p[x] == a T0[x] + b T1[x] + c T2[x] + ...
   * 
   * @param coeffs
   * @return */
  public static ScalarUnaryOperator of(Tensor coeffs) {
    return new ChebyshevClenshaw(ScalarUnaryOperator.IDENTITY, coeffs);
  }

  public static ScalarUnaryOperator forward(Clip clip, Tensor coef) {
    Scalar den = clip.width().multiply(RationalScalar.HALF);
    Scalar aff = clip.min().add(den);
    return of(x -> x.subtract(aff).divide(den), coef);
  }

  public static ScalarUnaryOperator inverse(Scalar num, Tensor coef) {
    return of(x -> num.divide(x).subtract(RealScalar.ONE), coef);
  }

  private static ScalarUnaryOperator of(ScalarUnaryOperator suo, Tensor _coef) {
    return new ChebyshevClenshaw(suo, _coef);
  }

  // ---
  private final ScalarUnaryOperator suo;
  private final Scalar[] a;

  private ChebyshevClenshaw(ScalarUnaryOperator suo, Tensor coeffs) {
    this.suo = suo;
    a = ScalarArray.ofVector(Reverse.of(coeffs));
  }

  @Override
  public Scalar apply(Scalar _x) {
    Scalar x1 = suo.apply(_x);
    Scalar x2 = x1.add(x1);
    int k = 0;
    Scalar bk2 = RealScalar.ZERO;
    Scalar bk1 = RealScalar.ZERO;
    Scalar bk0 = a[k++];
    for (; k < a.length; ++k) {
      bk2 = bk1;
      bk1 = bk0;
      bk0 = x2.multiply(bk1).subtract(bk2).add(a[k]);
    }
    return bk0.subtract(bk2).add(a[a.length - 1]).multiply(RationalScalar.HALF);
  }
}
