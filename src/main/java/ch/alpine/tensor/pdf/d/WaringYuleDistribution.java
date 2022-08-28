// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.gam.Beta;

public class WaringYuleDistribution extends AbstractDiscreteDistribution implements Serializable {
  public static Distribution of(Scalar alpha) {
    return new WaringYuleDistribution(alpha);
  }

  public static Distribution of(Number alpha) {
    return of(RealScalar.of(alpha));
  }

  // ---
  private final Scalar alpha;

  private WaringYuleDistribution(Scalar alpha) {
    this.alpha = alpha;
  }

  @Override
  public int lowerBound() {
    return 0;
  }

  @Override
  protected Scalar protected_p_equals(int x) {
    return 0 <= x //
        ? Beta.of(alpha.add(RealScalar.ONE), RealScalar.of(x + 1)).multiply(alpha)
        : RealScalar.ZERO;
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    return private_cdf(Ceiling.FUNCTION.apply(x.subtract(RealScalar.ONE)));
  }

  @Override
  public Scalar p_lessEquals(Scalar x) {
    return private_cdf(Floor.FUNCTION.apply(x));
  }

  private Scalar private_cdf(Scalar x) {
    return RealScalar.ONE.subtract(Beta.of(alpha, x.add(RealScalar.TWO)).multiply(alpha));
  }

  @Override
  public Scalar quantile(Scalar p) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar mean() {
    return Scalars.lessThan(RealScalar.ONE, alpha) //
        ? alpha.subtract(RealScalar.ONE).reciprocal()
        : DoubleScalar.INDETERMINATE;
  }

  @Override
  public Scalar variance() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("WaringYuleDistribution", alpha);
  }
}
