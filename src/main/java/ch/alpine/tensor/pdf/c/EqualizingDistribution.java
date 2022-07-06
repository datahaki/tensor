// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.itp.LinearBinaryAverage;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.pdf.d.CategoricalDistribution;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;

/** EqualizingDistribution is a continuous {@link CategoricalDistribution} */
public class EqualizingDistribution implements UnivariateDistribution, Serializable {
  /** Hint: distribution can be used for arc-length parameterization
   * 
   * @param unscaledPDF vector with non-negative weights over the numbers
   * [0, 1, 2, ..., unscaledPDF.length() - 1]
   * @return */
  public static Distribution fromUnscaledPDF(Tensor unscaledPDF) {
    return new EqualizingDistribution(unscaledPDF);
  }

  // ---
  private final CategoricalDistribution categoricalDistribution;

  private EqualizingDistribution(Tensor unscaledPDF) {
    categoricalDistribution = CategoricalDistribution.fromUnscaledPDF(unscaledPDF);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return categoricalDistribution.at(Floor.FUNCTION.apply(x));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return categoricalDistribution.mean().add(RationalScalar.HALF);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar xlo = Floor.FUNCTION.apply(x);
    Scalar ofs = Clips.interval(xlo, RealScalar.ONE.add(xlo)).rescale(x);
    return (Scalar) LinearBinaryAverage.INSTANCE.split( //
        categoricalDistribution.p_lessThan(xlo), //
        categoricalDistribution.p_lessEquals(xlo), ofs);
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    Scalar x_floor = categoricalDistribution.quantile(p);
    return x_floor.add(Clips.interval( //
        categoricalDistribution.p_lessThan(x_floor), //
        categoricalDistribution.p_lessEquals(x_floor)).rescale(p));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return categoricalDistribution.randomVariate(random).add(DoubleScalar.of(random.nextDouble()));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return Expectation.variance(categoricalDistribution).add(RationalScalar.of(1, 12));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("EqualizingDistribution", categoricalDistribution);
  }
}
