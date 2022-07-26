// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.erf.Erfc;
import ch.alpine.tensor.sca.erf.InverseErfc;
import ch.alpine.tensor.sca.pow.Sqrt;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BirnbaumSaundersDistribution.html">BirnbaumSaundersDistribution</a> */
public class BirnbaumSaundersDistribution extends AbstractContinuousDistribution implements Serializable {
  private static final Scalar _4 = RealScalar.of(4);
  private static final Scalar _5 = RealScalar.of(5);

  /** parameters may be instance of {@link Quantity} with identical units
   * 
   * @param alpha positive
   * @param lambda positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar lambda) {
    if (Scalars.lessThan(RealScalar.ZERO, alpha) && //
        Scalars.lessThan(RealScalar.ZERO, lambda))
      return new BirnbaumSaundersDistribution(alpha, lambda);
    throw new Throw(alpha, lambda);
  }

  /** @param alpha any real number
   * @param lambda positive
   * @return */
  public static Distribution of(Number alpha, Number lambda) {
    return of(RealScalar.of(alpha), RealScalar.of(lambda));
  }

  // ---
  private final Scalar alpha;
  private final Scalar lambda;
  private final Scalar alpha2;

  private BirnbaumSaundersDistribution(Scalar alpha, Scalar lambda) {
    this.alpha = alpha;
    this.lambda = lambda;
    alpha2 = alpha.multiply(alpha);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessEquals(x, RealScalar.ZERO))
      return RealScalar.ZERO;
    Scalar xl = x.multiply(lambda);
    Scalar div = alpha.multiply(Sqrt.FUNCTION.apply(xl));
    Scalar y = xl.subtract(RealScalar.ONE).divide(div);
    Scalar f1 = StandardNormalDistribution.INSTANCE.at(y);
    Scalar f2 = xl.add(RealScalar.ONE).divide(div).divide(x.add(x));
    return f1.multiply(f2);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    if (Scalars.lessEquals(x, RealScalar.ZERO))
      return RealScalar.ZERO;
    Scalar xl = x.multiply(lambda);
    Scalar div = alpha.multiply(Sqrt.FUNCTION.apply(xl));
    Scalar y = xl.subtract(RealScalar.ONE).divide(div).divide(Sqrt.FUNCTION.apply(RealScalar.TWO));
    return Erfc.FUNCTION.apply(y.negate()).multiply(RationalScalar.HALF);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.ONE.add(alpha2.multiply(RationalScalar.HALF)).divide(lambda);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return alpha2.multiply(_4.add(_5.multiply(alpha2))).divide(_4.multiply(lambda).multiply(lambda));
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    Scalar i = InverseErfc.FUNCTION.apply(p.add(p));
    Scalar ai = alpha.multiply(i);
    Scalar ai2 = ai.multiply(ai);
    return RealScalar.ONE.add(ai2.subtract(ai.multiply(Sqrt.FUNCTION.apply(RealScalar.TWO.add(ai2))))).divide(lambda);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("BirnbaumSaundersDistribution", alpha, lambda);
  }
}
