// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;

/** EqualizingDistribution is a continuous {@link EmpiricalDistribution} */
public class EqualizingDistribution implements //
    ContinuousDistribution, Serializable {
  /** Hint: distribution can be used for arc-length parameterization
   * 
   * @param unscaledPDF vector with non-negative weights over the numbers
   * [0, 1, 2, ..., unscaledPDF.length() - 1]
   * @return */
  public static Distribution fromUnscaledPDF(Tensor unscaledPDF) {
    return new EqualizingDistribution(unscaledPDF);
  }

  /***************************************************/
  private final EmpiricalDistribution empiricalDistribution;

  private EqualizingDistribution(Tensor unscaledPDF) {
    empiricalDistribution = (EmpiricalDistribution) EmpiricalDistribution.fromUnscaledPDF(unscaledPDF);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return empiricalDistribution.at(Floor.FUNCTION.apply(x));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return empiricalDistribution.mean().add(RationalScalar.HALF);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar xlo = Floor.FUNCTION.apply(x);
    Scalar ofs = Clips.interval(xlo, RealScalar.ONE.add(xlo)).rescale(x);
    return LinearInterpolation.of(Tensors.of( //
        empiricalDistribution.p_lessThan(xlo), //
        empiricalDistribution.p_lessEquals(xlo))).At(ofs);
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    Scalar x_floor = empiricalDistribution.quantile(p);
    return x_floor.add(Clips.interval( //
        empiricalDistribution.p_lessThan(x_floor), //
        empiricalDistribution.p_lessEquals(x_floor)).rescale(p));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return empiricalDistribution.randomVariate(random).add(DoubleScalar.of(random.nextDouble()));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return Expectation.variance(empiricalDistribution).add(RationalScalar.of(1, 12));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), empiricalDistribution);
  }
}
