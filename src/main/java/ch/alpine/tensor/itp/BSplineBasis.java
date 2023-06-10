// code by jph
package ch.alpine.tensor.itp;

import java.util.function.Function;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Sign;

/** Careful:
 * The support of Mathematica::BSplineBasis is over the unit interval [0, 1]
 * The tensor library centers BSplineBasis around zero, and the support
 * grows with the degree. The integral over BSplineBasis equals 1.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BSplineBasis.html">BSplineBasis</a> */
public class BSplineBasis implements ScalarUnaryOperator {
  private static final int CACHE_SIZE = 16;
  private static final Function<Integer, ScalarUnaryOperator> CACHE = Cache.of(BSplineBasis::new, CACHE_SIZE);

  /** @param degree
   * @return uniform B-spline basis function of given degree */
  public static ScalarUnaryOperator of(int degree) {
    return CACHE.apply(degree);
  }

  // ---
  private final Scalar deg;
  private final Scalar support;
  private final ScalarTensorFunction function;

  private BSplineBasis(int degree) {
    Integers.requirePositiveOrZero(degree);
    int ext = degree == 0 ? 1 : 0;
    Tensor knots = UnitVector.of(degree + 1 + degree + ext, degree);
    support = RealScalar.of(knots.length() - 1);
    function = BSplineFunctionString.of(degree, knots);
    this.deg = RealScalar.of(degree);
  }

  @Override
  public Scalar apply(Scalar t) {
    if (Sign.isNegative(t))
      return apply(t.negate());
    Scalar x = t.add(deg);
    return Scalars.lessThan(x, support) //
        ? (Scalar) function.apply(x)
        : x.zero();
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("BSplineBasis", deg);
  }
}
