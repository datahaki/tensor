// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.N;

/** Reference:
 * https://en.wikipedia.org/wiki/Dirac_delta_function */
public class DiracDeltaDistribution implements UnivariateDistribution, CentralMomentInterface, Serializable {
  /** @param value
   * @return distribution with entire concentration at given value, i.e.
   * all random variates give the constant value */
  public static Distribution of(Scalar value) {
    return new DiracDeltaDistribution(Objects.requireNonNull(value));
  }

  // ---
  private final Scalar value;
  private final Scalar infty;

  private DiracDeltaDistribution(Scalar value) {
    this.value = value;
    infty = N.DOUBLE.apply(value.zero()).reciprocal();
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return 0 == Scalars.compare(x, value) //
        ? infty
        : infty.zero();
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
    Clips.unit().requireInside(p);
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

  @Override // from CentralMomentInterface
  public Scalar centralMoment(int order) {
    return Boole.of(order == 0);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("DiracDeltaDistribution", value);
  }
}
