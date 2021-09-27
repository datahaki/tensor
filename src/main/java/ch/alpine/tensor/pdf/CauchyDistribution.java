// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Tan;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CauchyDistribution.html">CauchyDistribution</a> */
public class CauchyDistribution extends AbstractContinuousDistribution implements Serializable {
  private static final Distribution STANDARD = CauchyDistribution.of(RealScalar.ZERO, RealScalar.ONE);

  /** @param a
   * @param b positive
   * @return */
  public static Distribution of(Scalar a, Scalar b) {
    return new CauchyDistribution(Objects.requireNonNull(a), Sign.requirePositive(b));
  }

  /** @param a
   * @param b positive
   * @return */
  public static Distribution of(Number a, Number b) {
    return of(RealScalar.of(a), RealScalar.of(b));
  }

  /** @return CauchyDistribution[0, 1] */
  public static Distribution standard() {
    return STANDARD;
  }

  // ---
  private final Scalar a;
  private final Scalar b;

  private CauchyDistribution(Scalar a, Scalar b) {
    this.a = a;
    this.b = b;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar ax_b = x.subtract(a).divide(b);
    return RealScalar.ONE.add(ax_b.multiply(ax_b)).reciprocal().divide(Pi.VALUE).divide(b);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return ArcTan.of(b, x.subtract(a)).divide(Pi.VALUE).add(RationalScalar.HALF);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return DoubleScalar.INDETERMINATE;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return DoubleScalar.INDETERMINATE;
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    return Tan.FUNCTION.apply(p.add(p).subtract(RealScalar.ONE).multiply(Pi.HALF)).multiply(b).add(a);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s]", getClass().getSimpleName(), a, b);
  }
}
