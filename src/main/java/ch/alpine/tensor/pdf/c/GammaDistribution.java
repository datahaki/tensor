// code by jph
package ch.alpine.tensor.pdf.c;

import java.util.OptionalInt;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.LogGamma;
import ch.alpine.tensor.sca.pow.Power;

/** special cases of the Gamma distribution are
 * <code>
 * GammaDistribution[1, lambda] == ExponentialDistribution[1 / lambda]
 * GammaDistribution[k, lambda] == ErlangDistribution[k, 1 / lambda] for k positive integer
 * </code>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/GammaDistribution.html">GammaDistribution</a> */
public class GammaDistribution extends Gamma1Distribution {
  /** @param alpha shape positive real
   * @param beta scale positive real
   * @return GammaDistribution[alpha, beta] */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessThan(RealScalar.ZERO, alpha) && //
        Scalars.lessThan(RealScalar.ZERO, beta)) {
      OptionalInt optionalInt = Scalars.optionalInt(alpha);
      if (optionalInt.isPresent())
        return ErlangDistribution.of(optionalInt.orElseThrow(), beta.reciprocal());
      if (beta.equals(RealScalar.ONE))
        return new Gamma1Distribution(alpha);
      return new GammaDistribution(alpha, beta);
    }
    throw new Throw(alpha, beta);
  }

  /** @param alpha shape positive
   * @param beta scale positive
   * @return GammaDistribution[alpha, beta] */
  public static Distribution of(Number alpha, Number beta) {
    return of(RealScalar.of(alpha), RealScalar.of(beta));
  }

  // ---
  private final Scalar beta; // scale
  private final Scalar factor;

  @PackageTestAccess
  GammaDistribution(Scalar alpha, Scalar beta) {
    super(alpha);
    this.beta = beta;
    factor = Exp.FUNCTION.apply(Log.FUNCTION.apply(beta).multiply(alpha).add(LogGamma.FUNCTION.apply(alpha)).negate());
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x))
      return Exp.FUNCTION.apply(x.negate().divide(beta)) //
          .multiply(Power.of(x, alpha.subtract(RealScalar.ONE))).multiply(factor);
    return RealScalar.ZERO;
  }
  // CDF requires GammaRegularized

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return super.randomVariate(randomGenerator).multiply(beta);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return super.mean().multiply(beta);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return super.variance().multiply(beta).multiply(beta);
  }

  @Override // from StandardDeviationInterface
  public Scalar standardDeviation() {
    return super.standardDeviation().multiply(beta);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("GammaDistribution", alpha, beta);
  }
}
