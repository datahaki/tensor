// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TransformedDistribution.html">TransformedDistribution</a> */
public enum TransformedDistribution {
  ;
  /** @param distribution
   * @param offset
   * @return given distribution shifted by offset along the domain
   * @throws Exception if either parameter is null
   * @see TrapezoidalDistribution */
  public static Distribution shift(Distribution distribution, Scalar offset) {
    return new Shifted( //
        Objects.requireNonNull(distribution), //
        Objects.requireNonNull(offset));
  }

  private record Shifted(Distribution distribution, Scalar offset) implements UnivariateDistribution, //
      CentralMomentInterface, Serializable {
    @Override
    public Scalar at(Scalar x) {
      return PDF.of(distribution).at(x.subtract(offset));
    }

    @Override // from CDF
    public Scalar p_lessThan(Scalar x) {
      return CDF.of(distribution).p_lessThan(x.subtract(offset));
    }

    @Override // from CDF
    public Scalar p_lessEquals(Scalar x) {
      return CDF.of(distribution).p_lessEquals(x.subtract(offset));
    }

    @Override // from InverseCDF
    public Scalar quantile(Scalar p) {
      return InverseCDF.of(distribution).quantile(p).add(offset);
    }

    @Override // from RandomVariateInterface
    public Scalar randomVariate(RandomGenerator randomGenerator) {
      return RandomVariate.of(distribution, randomGenerator).add(offset);
    }

    @Override // from MeanInterface
    public Scalar mean() {
      return Mean.of(distribution).add(offset);
    }

    @Override // from VarianceInterface
    public Scalar variance() {
      return Variance.of(distribution);
    }

    @Override // from CentralMomentInterface
    public Scalar centralMoment(int order) {
      return CentralMoment.of(distribution, order);
    }

    @Override // from Object
    public String toString() {
      return MathematicaFormat.concise("TransformedDistribution", distribution, offset);
    }
  }
}
