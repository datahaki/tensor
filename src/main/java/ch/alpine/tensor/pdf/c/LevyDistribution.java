// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.erf.Erfc;
import ch.alpine.tensor.sca.erf.InverseErfc;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

public class LevyDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param mu
   * @param sigma positive
   * @return */
  public static Distribution of(Scalar mu, Scalar sigma) {
    return new LevyDistribution( //
        mu, //
        Sign.requirePositive(sigma));
  }

  /** @param mu
   * @param sigma positive
   * @return */
  public static Distribution of(Number mu, Number sigma) {
    return of(RealScalar.of(mu), RealScalar.of(sigma));
  }

  // ---
  private final Scalar mu;
  private final Scalar sigma;
  private final ScalarUnaryOperator power;

  private LevyDistribution(Scalar mu, Scalar sigma) {
    this.mu = mu;
    this.sigma = sigma;
    power = Power.function(RationalScalar.of(3, 2));
  }

  @Override
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(mu, x)) {
      Scalar f = sigma.divide(x.subtract(mu));
      Scalar f1 = Exp.FUNCTION.apply(f.divide(RealScalar.of(-2)));
      Scalar f2 = power.apply(f);
      return f1.multiply(f2).divide(Sqrt.FUNCTION.apply(Pi.TWO)).divide(sigma);
    }
    return RealScalar.ZERO;
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    if (Scalars.lessThan(mu, x)) {
      Scalar f = sigma.divide(x.subtract(mu));
      return Erfc.FUNCTION.apply(Sqrt.FUNCTION.apply(f.divide(RealScalar.TWO)));
    }
    return RealScalar.ZERO;
  }

  @Override
  public Scalar mean() {
    return Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(mu));
  }

  @Override
  public Scalar variance() {
    return Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(sigma));
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    Scalar f = InverseErfc.FUNCTION.apply(p);
    Scalar f2 = f.multiply(f);
    return sigma.divide(f2.add(f2)).add(mu);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("LevyDistribution", mu, sigma);
  }
}
