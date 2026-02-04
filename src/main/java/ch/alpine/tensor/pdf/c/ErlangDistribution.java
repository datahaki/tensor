// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.opt.fnd.FindRoot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.StandardDeviationInterface;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Factorial;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** ErlangDistribution[k, lambda] == GammaDistribution[k, 1 / lambda]
 * 
 * <p>The CDF of the Erlang-distribution[k, lambda] is the function
 * GammaRegularized[k, 0, x * lambda]
 * which is not yet available in the tensor library.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ErlangDistribution.html">ErlangDistribution</a> */
public class ErlangDistribution extends AbstractContinuousDistribution implements //
    StandardDeviationInterface, Serializable {
  /** @param k positive integer
   * @param lambda, may be instance of {@link Quantity}
   * @return
   * @throws Exception if k is negative or zero */
  public static Distribution of(int k, Scalar lambda) {
    return k == 1 //
        ? ExponentialDistribution.of(lambda)
        : new ErlangDistribution(Integers.requirePositive(k), lambda);
  }

  /** @param k positive integer
   * @param lambda
   * @return */
  public static Distribution of(int k, Number lambda) {
    return of(k, RealScalar.of(lambda));
  }

  // ---
  private final int k;
  private final Scalar _k;
  private final Scalar lambda;
  private final Scalar factor;
  private final ScalarUnaryOperator power;
  private final Scalar mean;

  @PackageTestAccess
  ErlangDistribution(int k, Scalar lambda) {
    this.k = k;
    _k = RealScalar.of(k);
    this.lambda = lambda;
    factor = Power.of(lambda, k).divide(Factorial.of(k - 1));
    power = Power.function(k - 1);
    mean = _k.divide(lambda);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Sign.isNegative(x))
      return lambda.zero();
    Scalar xlambda = x.multiply(lambda);
    Scalar exp = Exp.FUNCTION.apply(xlambda.negate());
    return Times.of(exp, power.apply(x), factor);
  }

  @Override // from CDF requires GammaRegularized
  public Scalar p_lessThan(Scalar x) {
    Scalar sum = RealScalar.ONE;
    Scalar fac = RealScalar.ONE;
    Scalar xlambda = x.multiply(lambda);
    for (int n = 1; n < k; ++n) {
      fac = fac.multiply(xlambda).divide(RealScalar.of(n));
      sum = sum.add(fac);
    }
    Scalar exp = Exp.FUNCTION.apply(xlambda.negate());
    return RealScalar.ONE.subtract(sum.multiply(exp));
  }

  @Override // from InverseCDF
  public Scalar protected_quantile(Scalar p) {
    return FindRoot.of(x -> p_lessThan(x).subtract(p)).above(mean.zero(), mean);
  }

  @Override // from AbstractContinuousDistribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return Log.FUNCTION.apply(RandomVariate.stream(UniformDistribution.unit(), randomGenerator) //
        .limit(k).reduce(Scalar::multiply).orElseThrow()).divide(lambda).negate();
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return mean;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return _k.divide(lambda.multiply(lambda));
  }

  @Override // from StandardDeviationInterface
  public Scalar standardDeviation() {
    return Sqrt.FUNCTION.apply(_k).divide(lambda);
  }

  @Override // from UnivariateDistribution
  public Clip support() {
    return Clips.positive(Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(mean)));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ErlangDistribution", _k, lambda);
  }
}
