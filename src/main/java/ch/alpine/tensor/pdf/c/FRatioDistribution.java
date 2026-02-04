// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.gam.Beta;
import ch.alpine.tensor.sca.pow.Power;

/** CDF requires BetaRegularized */
public class FRatioDistribution implements Distribution, //
    PDF, MeanInterface, VarianceInterface, Serializable {
  /** @param n strictly positive real scalar
   * @param m strictly positive real scalar
   * @return */
  public static Distribution of(Scalar n, Scalar m) {
    if (Scalars.lessThan(RealScalar.ZERO, n) && //
        Scalars.lessThan(RealScalar.ZERO, m))
      return new FRatioDistribution(n, m);
    throw new Throw(n, m);
  }

  public static Distribution of(Number n, Number m) {
    return of(RealScalar.of(n), RealScalar.of(m));
  }

  // ---
  private final Scalar n;
  private final Scalar m;
  private final Scalar scale;
  private final Scalar f1;
  private final Scalar f2;
  private final ScalarUnaryOperator power;
  private final Distribution rvin;
  private final Distribution rvim;

  private FRatioDistribution(Scalar n, Scalar m) {
    this.n = n;
    this.m = m;
    Scalar n2 = n.multiply(RationalScalar.HALF);
    Scalar m2 = m.multiply(RationalScalar.HALF);
    scale = Beta.of(n2, m2);
    f1 = Power.of(n, n2);
    f2 = Power.of(m, m2);
    power = Power.function(n2.add(m2).negate());
    rvin = ChiSquareDistribution.of(n);
    rvim = ChiSquareDistribution.of(m);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar f3 = Power.of(x, n.divide(RealScalar.TWO).subtract(RealScalar.ONE));
      Scalar f4 = power.apply(x.multiply(n).add(m));
      return Times.of(f1, f2, f3, f4).divide(scale);
    }
    return RealScalar.ZERO;
  }

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    Scalar U = rvin.randomVariate(randomGenerator).divide(n);
    Scalar V = rvim.randomVariate(randomGenerator).divide(m);
    return U.divide(V);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return Scalars.lessThan(RealScalar.TWO, m) //
        ? m.divide(m.subtract(RealScalar.TWO))
        : DoubleScalar.INDETERMINATE;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    if (Scalars.lessThan(RealScalar.of(4), m)) {
      Scalar m_2 = m.subtract(RealScalar.TWO);
      Scalar num = Times.of(RealScalar.TWO, m, m, m_2.add(n));
      Scalar den = Times.of(m.subtract(RealScalar.of(4)), m_2, m_2, n);
      return num.divide(den);
    }
    return DoubleScalar.INDETERMINATE;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("FRatioDistribution", n, m);
  }
}
