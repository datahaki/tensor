// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class HalfNormalDistribution extends AbstractContinuousDistribution implements Serializable {
  private final Scalar theta;

  public HalfNormalDistribution(Scalar theta) {
    this.theta = theta;
  }

  // TODO Auto-generated method stub
  @Override
  public Scalar at(Scalar x) {
    return null;
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    return null;
  }

  @Override
  public Scalar mean() {
    return theta.reciprocal();
  }

  @Override
  public Scalar variance() {
    return Pi.HALF.subtract(RealScalar.ONE).divide(theta.multiply(theta));
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    return null;
  }

  @Override
  public Clip support() {
    return Clips.positive(Double.POSITIVE_INFINITY);
  }
}
