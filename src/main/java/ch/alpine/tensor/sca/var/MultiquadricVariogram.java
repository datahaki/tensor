// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Quotes:
 * "Multiquadrics are said to be less sensitive to the choice of r0 than some other
 * functional forms."
 * 
 * "In general, r0 should be larger than the typical separation of points but smaller than
 * the 'outer scale' or feature size of the function that you are interpolating. There can
 * be several orders of magnitude difference between the interpolation accuracy with a good
 * choice for r0, versus a poor choice, so it is definitely worth some experimentation. One
 * way to experiment is to construct an RBF interpolator omitting one data point at a time
 * and measuring the interpolation error at the omitted point."
 * 
 * Reference:
 * "Radial Basis Functions in General Use", eq (3.7.5)
 * in NR, 2007
 * 
 * <p>The unit of r0 is also the unit of the returned values.
 * For example, if r0 has unit "m" then a returned value has unit "m". */
public class MultiquadricVariogram implements ScalarUnaryOperator {
  /** @param r0 non-negative */
  public static ScalarUnaryOperator of(Scalar r0) {
    return Scalars.isZero(r0) //
        ? Abs.FUNCTION
        : new MultiquadricVariogram(Sign.requirePositive(r0));
  }

  // ---
  private final Scalar r0;
  private final Scalar r0_squared;

  protected MultiquadricVariogram(Scalar r0) {
    this.r0 = r0;
    r0_squared = r0.multiply(r0);
  }

  @Override // from TensorNorm
  public Scalar apply(Scalar r) {
    return Sqrt.FUNCTION.apply(r0_squared.add(r.multiply(r)));
  }

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("MultiquadricVariogram", r0);
  }
}
