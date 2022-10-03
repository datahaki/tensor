// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TransformedDistribution.html">TransformedDistribution</a> */
public enum TransformedDistribution {
  ;
  /** @param distribution
   * @param offset
   * @return */
  public static Distribution shift(Distribution distribution, Scalar offset) {
    return new Shifted( //
        Objects.requireNonNull(distribution), //
        Objects.requireNonNull(offset));
  }

  private static class Shifted implements Distribution, //
      PDF, CDF, InverseCDF, RandomVariateInterface, MeanInterface, VarianceInterface, CentralMomentInterface, Serializable {
    private final Distribution distribution;
    private final Scalar offset;

    public Shifted(Distribution distribution, Scalar offset) {
      this.distribution = distribution;
      this.offset = offset;
    }

    @Override
    public Scalar at(Scalar x) {
      return PDF.of(distribution).at(x.subtract(offset));
    }

    @Override
    public Scalar p_lessThan(Scalar x) {
      return CDF.of(distribution).p_lessThan(x.subtract(offset));
    }

    @Override
    public Scalar p_lessEquals(Scalar x) {
      return CDF.of(distribution).p_lessEquals(x.subtract(offset));
    }

    @Override
    public Scalar quantile(Scalar p) {
      return InverseCDF.of(distribution).quantile(p).add(offset);
    }

    @Override
    public Scalar randomVariate(Random random) {
      return RandomVariate.of(distribution, random).add(offset);
    }

    @Override
    public Scalar mean() {
      return Mean.of(distribution).add(offset);
    }

    @Override
    public Scalar variance() {
      return Variance.of(distribution);
    }

    @Override
    public Scalar centralMoment(int order) {
      return CentralMoment.of(distribution, order);
    }

    @Override
    public String toString() {
      return MathematicaFormat.concise("TransformedDistribution", distribution, offset);
    }
  }
}
