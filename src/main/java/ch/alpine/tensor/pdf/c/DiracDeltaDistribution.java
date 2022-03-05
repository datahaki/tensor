// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.UnivariateDistribution;

/** Reference:
 * https://en.wikipedia.org/wiki/Dirac_delta_function */
public class DiracDeltaDistribution implements UnivariateDistribution, Serializable {
  /** @param value
   * @return */
  public static Distribution of(Scalar value) {
    return new DiracDeltaDistribution(Objects.requireNonNull(value));
  }

  // ---
  private final Scalar value;

  private DiracDeltaDistribution(Scalar value) {
    this.value = value;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return x.equals(value) //
        ? DoubleScalar.POSITIVE_INFINITY
        : RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Boole.of(Scalars.lessThan(value, x));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return Boole.of(Scalars.lessEquals(value, x));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return value;
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return value;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return value;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return RealScalar.ZERO;
  }
}
