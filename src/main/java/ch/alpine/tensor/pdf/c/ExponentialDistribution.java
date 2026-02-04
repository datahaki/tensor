// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.StandardDeviationInterface;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Subfactorial;
import ch.alpine.tensor.sca.pow.Power;

/** Characteristics of an exponential distribution:
 * <ul>
 * <li>Random variates are non-negative real numbers.
 * <li>The distribution depends on a rate-of-decay parameter lambda.
 * Increasing lambda results in a distribution with mean moving towards 0,
 * i.e. faster decay.
 * </ul>
 * 
 * <p>The InverseCDF at p == 1 evaluates to DoubleScalar.POSITIVE_INFINITY.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ExponentialDistribution.html">ExponentialDistribution</a> */
public class ExponentialDistribution implements UnivariateDistribution, //
    CentralMomentInterface, StandardDeviationInterface, Serializable {
  private static final Distribution STANDARD = of(RealScalar.ONE);

  /** @param lambda positive, may be instance of {@link Quantity}
   * @return exponential distribution with scale inversely proportional to parameter lambda */
  public static Distribution of(Scalar lambda) {
    return new ExponentialDistribution(Sign.requirePositive(lambda));
  }

  /** @param lambda positive
   * @return exponential distribution with scale inversely proportional to parameter lambda */
  public static Distribution of(Number lambda) {
    return of(RealScalar.of(lambda));
  }

  /** @return exponential distribution with mean and variance equal to 1 */
  public static Distribution standard() {
    return STANDARD;
  }

  // ---
  private final Scalar lambda;
  private final Scalar lambda_negate;

  private ExponentialDistribution(Scalar lambda) {
    this.lambda = lambda;
    lambda_negate = lambda.negate();
  }

  @Override // from UnivariateDistribution
  public Clip support() {
    return Clips.positive(Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(lambda).negate()));
  }

  @Override // from AbstractContinuousDistribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return RealScalar.of(randomGenerator.nextExponential()).divide(lambda);
  }

  @Override // from AbstractContinuousDistribution
  public Scalar quantile(Scalar p) {
    Clips.unit().requireInside(p);
    return Log.FUNCTION.apply(RealScalar.ONE.subtract(p)).divide(lambda_negate);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return lambda.reciprocal();
  }

  @Override // from StandardDeviationInterface
  public Scalar standardDeviation() {
    return lambda.reciprocal();
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return lambda.multiply(lambda).reciprocal();
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar prod = x.multiply(lambda_negate);
    if (QuantityUnit.of(prod).equals(Unit.ONE))
      return Sign.isPositiveOrZero(x) //
          ? Exp.FUNCTION.apply(prod).multiply(lambda)
          : lambda.zero();
    throw new Throw(x);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar prod = x.multiply(lambda_negate);
    if (QuantityUnit.of(prod).equals(Unit.ONE))
      return Sign.isPositive(x) //
          ? RealScalar.ONE.subtract(Exp.FUNCTION.apply(prod))
          : RealScalar.ZERO;
    throw new Throw(x);
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from CentralMomentInterface
  public Scalar centralMoment(int order) {
    return Subfactorial.of(order).divide(Power.of(lambda, order));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ExponentialDistribution", lambda);
  }
}
