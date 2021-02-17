// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Tan;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CauchyDistribution.html">CauchyDistribution</a> */
public class CauchyDistribution extends AbstractContinuousDistribution implements //
    InverseCDF, Serializable {
  private static final long serialVersionUID = -7860035228725423560L;

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

  /***************************************************/
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

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return _quantile(Clips.unit().requireInside(p));
  }

  private Scalar _quantile(Scalar p) {
    return Tan.FUNCTION.apply(p.add(p).subtract(RealScalar.ONE).multiply(Pi.HALF)).multiply(b).add(a);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar randomVariate(double reference) {
    return _quantile(DoubleScalar.of(reference));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s]", getClass().getSimpleName(), a, b);
  }
}
