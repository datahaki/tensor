// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.gam.Pochhammer;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** CDF requires GammaRegularized
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/NakagamiDistribution.html">NakagamiDistribution</a> */
public class NakagamiDistribution implements Distribution, //
    PDF, MeanInterface, VarianceInterface, Serializable {
  /** @param mu strictly positive
   * @param w strictly positive
   * @return */
  public static Distribution of(Scalar mu, Scalar w) {
    if (Scalars.lessThan(RealScalar.ZERO, mu) && //
        Scalars.lessThan(RealScalar.ZERO, w))
      return new NakagamiDistribution(mu, w);
    throw new Throw(mu, w);
  }

  /** @param mu strictly positive
   * @param w strictly positive
   * @return */
  public static Distribution of(Number mu, Number w) {
    return of(RealScalar.of(mu), RealScalar.of(w));
  }

  // ---
  private final Scalar mu;
  private final Scalar w;
  private final Distribution distribution;

  private NakagamiDistribution(Scalar mu, Scalar w) {
    this.mu = mu;
    this.w = w;
    distribution = GammaDistribution.of(mu, w.divide(mu));
  }

  @Override
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar f1 = RealScalar.TWO;
      Scalar f2 = Exp.FUNCTION.apply(x.multiply(x).multiply(mu).divide(w).negate());
      Scalar f3 = Power.of(x, mu.add(mu).subtract(RealScalar.ONE));
      Scalar f4 = Power.of(mu.divide(w), mu);
      return Times.of(f1, f2, f3, f4).divide(Gamma.FUNCTION.apply(mu));
    }
    return RealScalar.ZERO;
  }

  @Override
  public Scalar mean() {
    return Sqrt.FUNCTION.apply(w.divide(mu)).multiply(Pochhammer.of(mu, RationalScalar.HALF));
  }

  @Override
  public Scalar variance() {
    Scalar f = Pochhammer.of(mu, RationalScalar.HALF);
    return w.subtract(w.divide(mu).multiply(f).multiply(f));
  }

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return Sqrt.FUNCTION.apply(distribution.randomVariate(randomGenerator));
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("NakagamiDistribution", mu, w);
  }
}
