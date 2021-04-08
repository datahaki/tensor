// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.LogGamma;
import ch.ethz.idsc.tensor.sca.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ChiSquareDistribution.html">ChiSquareDistribution</a> */
public class ChiSquareDistribution implements ContinuousDistribution, Serializable {
  /** @param nu positive real
   * @return
   * @throws Exception if nu is not positive or not an instance of {@link RealScalar} */
  public static Distribution of(Scalar nu) {
    if (Scalars.lessThan(RealScalar.ZERO, nu))
      return new ChiSquareDistribution(nu);
    throw TensorRuntimeException.of(nu);
  }

  /** @param nu positive real
   * @return */
  public static Distribution of(Number nu) {
    return of(RealScalar.of(nu));
  }

  /***************************************************/
  private final Scalar nu;
  private final Scalar nu2;
  private final Scalar log;

  private ChiSquareDistribution(Scalar nu) {
    this.nu = nu;
    nu2 = nu.multiply(RationalScalar.HALF);
    log = Log.FUNCTION.apply(RealScalar.TWO).multiply(nu2).add(LogGamma.FUNCTION.apply(nu2));
  }

  @Override
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x))
      return Exp.FUNCTION.apply(log.add(x.multiply(RationalScalar.HALF)).negate()) //
          .multiply(Power.of(x, nu2.subtract(RealScalar.ONE)));
    return RealScalar.ZERO;
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar p_lessEquals(Scalar x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar quantile(Scalar p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar randomVariate(Random random) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar mean() {
    return nu;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return nu.add(nu);
  }

  @Override
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), nu);
  }
}
