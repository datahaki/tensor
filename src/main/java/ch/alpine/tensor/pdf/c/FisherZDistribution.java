// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

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
import ch.alpine.tensor.sca.gam.Beta;
import ch.alpine.tensor.sca.pow.Power;

/** CDF requires BetaRegularized
 * Mean requires HypergeometricPFQ */
public class FisherZDistribution implements Distribution, //
    PDF, MeanInterface, VarianceInterface, Serializable {
  /** @param n strictly positive
   * @param m strictly positive
   * @return */
  public static Distribution of(Scalar n, Scalar m) {
    if (Scalars.lessThan(RealScalar.ZERO, n) && //
        Scalars.lessThan(RealScalar.ZERO, m))
      return new FisherZDistribution(n, m);
    throw new Throw(n, m);
  }

  public static Distribution of(Number n, Number m) {
    return of(RealScalar.of(n), RealScalar.of(m));
  }

  // ---
  private final Scalar n;
  private final Scalar m;
  private final Scalar scale;

  private FisherZDistribution(Scalar n, Scalar m) {
    this.n = n;
    this.m = m;
    scale = Beta.of(n.multiply(RationalScalar.HALF), m.multiply(RationalScalar.HALF));
  }

  @Override
  public Scalar at(Scalar x) {
    Scalar f1 = Exp.FUNCTION.apply(n.multiply(x));
    Scalar f2 = Power.of(m, m.multiply(RationalScalar.HALF));
    Scalar f3 = Power.of(n, n.multiply(RationalScalar.HALF));
    Scalar f4 = Power.of(m.add(Exp.FUNCTION.apply(x.add(x)).multiply(n)), n.add(m).multiply(RationalScalar.HALF).negate());
    return Times.of(RealScalar.TWO, f1, f2, f3, f4).divide(scale);
  }

  @Override
  public Scalar mean() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Scalar variance() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("FisherZDistribution", n, m);
  }
}
