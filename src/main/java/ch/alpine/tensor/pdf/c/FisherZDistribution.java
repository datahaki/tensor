// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Beta;
import ch.alpine.tensor.sca.pow.Power;

/** CDF requires BetaRegularized
 * Mean requires HypergeometricPFQ */
public class FisherZDistribution implements Distribution, //
    PDF, Serializable {
  /** @param n strictly positive real scalar
   * @param m strictly positive real scalar
   * @return */
  public static Distribution of(Scalar n, Scalar m) {
    if (Scalars.lessThan(RealScalar.ZERO, n) && //
        Scalars.lessThan(RealScalar.ZERO, m))
      return new FisherZDistribution(n, m);
    throw new Throw(n, m);
  }

  /** @param n strictly positive
   * @param m strictly positive
   * @return */
  public static Distribution of(Number n, Number m) {
    return of(RealScalar.of(n), RealScalar.of(m));
  }

  // ---
  private final Scalar n;
  private final Scalar m;
  private final Scalar scale;
  private final ScalarUnaryOperator power;
  private final Distribution rvi;

  private FisherZDistribution(Scalar n, Scalar m) {
    this.n = n;
    this.m = m;
    Scalar n_2 = n.multiply(Rational.HALF);
    Scalar m_2 = m.multiply(Rational.HALF);
    scale = Times.of(RealScalar.TWO, Power.of(n, n_2), Power.of(m, m_2)).divide(Beta.of(n_2, m_2));
    power = Power.function(n_2.add(m_2).negate());
    rvi = FRatioDistribution.of(n, m);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar factor = power.apply(m.add(Exp.FUNCTION.apply(x.add(x)).multiply(n)));
    return Scalars.isZero(factor) //
        ? RealScalar.ZERO
        : Times.of(scale, Exp.FUNCTION.apply(n.multiply(x)), factor);
  }

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return Log.FUNCTION.apply(rvi.randomVariate(randomGenerator)).multiply(Rational.HALF);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("FisherZDistribution", n, m);
  }
}
