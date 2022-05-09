// code by jph
package ch.alpine.tensor.itp;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FindRoot.html">FindRoot</a> */
public class FindRoot implements Serializable {
  private static final int MAX_ITERATIONS = 128;
  private static final Scalar HALF = RealScalar.of(0.5);

  /** @param function
   * @return */
  public static FindRoot of(ScalarUnaryOperator function) {
    return of(function, Tolerance.CHOP);
  }

  /** @param function
   * @param chop accuracy that determines function(x) is sufficiently close to 0
   * @return */
  public static FindRoot of(ScalarUnaryOperator function, Chop chop) {
    return new FindRoot(function, chop);
  }

  // ---
  private final ScalarUnaryOperator function;
  private final Chop chop;

  /** @param function
   * @param chop
   * @param predicate for instance Sign::isPositive */
  private FindRoot(ScalarUnaryOperator function, Chop chop) {
    this.function = function;
    this.chop = chop;
  }

  /** @param x0
   * @param x1
   * @return x between x0 and x1 so that function(x) == 0 with given chop accuracy
   * @throws Exception if function(x0) and function(x1) have the same sign unequal to zero */
  public Scalar between(Scalar x0, Scalar x1) {
    return between(x0, x1, function.apply(x0), function.apply(x1));
  }

  /** @param x0
   * @param x1
   * @param y0 == function(x0)
   * @param y1 == function(x1)
   * @return x between x0 and x1 so that function(x) == 0 with given chop accuracy
   * @throws Exception if y0 and y1 have the same sign unequal to zero */
  public Scalar between(Scalar x0, Scalar x1, Scalar y0, Scalar y1) {
    if (chop.isZero(y0))
      return x0;
    if (chop.isZero(y1))
      return x1;
    // ---
    Scalar s0 = Sign.FUNCTION.apply(y0); // s0 is never 0
    Scalar s1 = Sign.FUNCTION.apply(y1); // s1 is never 0
    if (s0.equals(s1))
      throw TensorRuntimeException.of(x0, x1, y0, y1);
    // ---
    for (int index = 0; index < MAX_ITERATIONS; ++index) {
      Scalar xn = index % 2 == 0 //
          ? (Scalar) LinearBinaryAverage.INSTANCE.split(x0, x1, HALF)
          : FindRoot.linear(x0, x1, y0, y1);
      // ---
      Scalar yn = function.apply(xn);
      // ---
      if (chop.isZero(yn)) {
        // System.out.println(index);
        return xn;
      }
      Scalar sn = Sign.FUNCTION.apply(yn); // sn is never 0
      if (s0.equals(sn)) {
        x0 = xn;
        y0 = yn;
        // s0 == sn
      } else {
        x1 = xn;
        y1 = yn;
        // s1 == sn
      }
    }
    throw TensorRuntimeException.of(x0, x1, y0, y1);
  }

  /** Function is equivalent to
   * <pre>
   * Fit.polynomial(Tensors.of(x0, x1), Tensors.of(y0, y1), 1).roots().Get(0);
   * </pre>
   * 
   * Functionality is implemented explicitly for speed.
   * 
   * @param x0
   * @param x1
   * @param y0
   * @param y1
   * @return (x0 y1 - x1 y0) / (y1 - y0) */
  @PackageTestAccess
  static Scalar linear(Scalar x0, Scalar x1, Scalar y0, Scalar y1) {
    return x0.multiply(y1).subtract(x1.multiply(y0)).divide(y1.subtract(y0));
  }
}
