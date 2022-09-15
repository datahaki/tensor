// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import java.util.stream.DoubleStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.ScalarArray;

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
  public static ScalarUnaryOperator forward(Scalar den, Scalar aff, double... coef) {
    return of(x -> x.divide(den).subtract(aff), coef);
  }

  public static ScalarUnaryOperator reverse(Scalar num, Scalar aff, double... coef) {
    return of(x -> num.divide(x).subtract(aff), coef);
  }

  private static ScalarUnaryOperator of(ScalarUnaryOperator suo, double[] _coef) {
    return new ChebyshevClenshaw(suo, DoubleStream.of(_coef).mapToObj(RealScalar::of).toArray(Scalar[]::new));
  }

  public static ScalarUnaryOperator of(Tensor coeffs) {
    return new ChebyshevClenshaw(ScalarUnaryOperator.IDENTITY, ScalarArray.ofVector(coeffs));
  }

  // ---
  private final ScalarUnaryOperator suo;
  private final Scalar[] a;

  private ChebyshevClenshaw(ScalarUnaryOperator suo, Scalar[] array) {
    this.suo = suo;
    a = array;
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
    return bk0.subtract(bk2).multiply(RationalScalar.HALF);
  }
}
