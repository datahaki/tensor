// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
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
  public static Distribution of(Scalar n, Scalar m) {
    return new FRatioDistribution(n, m);
  }

  public static Distribution of(Number n, Number m) {
    return new FRatioDistribution(RealScalar.of(n), RealScalar.of(m));
  }

  // ---
  private final Scalar n;
  private final Scalar m;
  private final Scalar scale;

  private FRatioDistribution(Scalar n, Scalar m) {
    this.n = n;
    this.m = m;
    scale = Beta.of(n.multiply(RationalScalar.HALF), m.multiply(RationalScalar.HALF));
  }

  @Override
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar f1 = Power.of(m, m.divide(RealScalar.TWO));
      Scalar f2 = Power.of(n, n.divide(RealScalar.TWO));
      Scalar f3 = Power.of(x, n.divide(RealScalar.TWO).subtract(RealScalar.ONE));
      Scalar f4 = Power.of(x.multiply(n).add(m), n.add(m).divide(RealScalar.TWO).negate());
      return Times.of(f1, f2, f3, f4).divide(scale);
    }
    return RealScalar.ZERO;
  }

  @Override
  public Scalar mean() {
    return Scalars.lessThan(RealScalar.TWO, m) //
        ? m.divide(m.subtract(RealScalar.TWO))
        : DoubleScalar.INDETERMINATE;
  }

  @Override
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
