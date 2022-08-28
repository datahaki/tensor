// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import java.util.stream.DoubleStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

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
 * @param x argument to the polynomial */
class Chebyshev implements ScalarUnaryOperator {
  public static Chebyshev of(double... values) {
    return new Chebyshev(values);
  }

  private final Scalar[] coef;

  public Chebyshev(double[] _coef) {
    coef = DoubleStream.of(_coef).mapToObj(RealScalar::of).toArray(Scalar[]::new);
  }

  @Override
  public Scalar apply(Scalar x) {
    Scalar b2;
    int p = 0;
    Scalar b0 = coef[p++];
    Scalar b1 = RealScalar.ZERO;
    int i = coef.length - 1;
    do {
      b2 = b1;
      b1 = b0;
      b0 = x.multiply(b1).subtract(b2).add(coef[p++]);
    } while (--i > 0);
    return b0.subtract(b2).multiply(RationalScalar.HALF);
  }

  double run(double y) {
    return apply(RealScalar.of(y)).number().doubleValue();
  }
}
