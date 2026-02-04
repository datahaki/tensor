// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.opt.fnd.FindRoot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.bes.BesselK;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.gam.Pochhammer;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/KDistribution.html">KDistribution</a> */
public class KDistribution extends AbstractContinuousDistribution implements Serializable {
  private static final Scalar _4 = RealScalar.of(4.0);

  /** @param v positive
   * @param w positive
   * @return K distribution with shape parameters v and w */
  public static Distribution of(Scalar v, Scalar w) {
    return new KDistribution( //
        Sign.requirePositive(v), //
        Sign.requirePositive(w));
  }

  /** @param v positive
   * @param w standard deviation
   * @return K distribution with shape parameters v and w */
  public static Distribution of(Number v, Number w) {
    return of(RealScalar.of(v), RealScalar.of(w));
  }

  // ---
  private final Scalar v;
  private final Scalar w;
  private final Scalar v_w;
  private final Scalar pow1;
  private final Scalar pow2;
  private final Scalar mean;
  private final Scalar variance;

  private KDistribution(Scalar v, Scalar w) {
    this.v = v;
    this.w = w;
    v_w = v.divide(w);
    Scalar p = Pochhammer.of(v, RationalScalar.HALF);
    pow1 = Power.of(v_w, v.add(RealScalar.ONE).multiply(RationalScalar.HALF)).multiply(_4);
    pow2 = Power.of(v_w, v.multiply(RationalScalar.HALF)).multiply(RealScalar.TWO);
    mean = Times.of( //
        RationalScalar.HALF, //
        Sqrt.FUNCTION.apply(Pi.VALUE), //
        Sqrt.FUNCTION.apply(v_w.reciprocal()), //
        p);
    variance = RealScalar.ONE.subtract(p.multiply(p).multiply(Pi.VALUE).divide(_4.multiply(v))).multiply(w);
  }

  @Override
  public Clip support() {
    return Clips.positive(Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(mean)));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar f1 = Power.of(x, v);
      Scalar f2 = BesselK.of( //
          v.subtract(RealScalar.ONE), //
          Sqrt.FUNCTION.apply(v_w).multiply(x).multiply(RealScalar.TWO));
      return Times.of(pow1, f1, f2).divide(Gamma.FUNCTION.apply(v));
    }
    return RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar f1 = Power.of(x, v);
      Scalar f2 = BesselK.of(v, Sqrt.FUNCTION.apply(v_w).multiply(x).multiply(RealScalar.TWO));
      return RealScalar.ONE.subtract(Times.of(pow2, f1, f2).divide(Gamma.FUNCTION.apply(v)));
    }
    return RealScalar.ZERO;
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    if (p.equals(RealScalar.ONE))
      return DoubleScalar.POSITIVE_INFINITY;
    return FindRoot.of(x -> p_lessThan(x).subtract(p)).above(mean.zero(), mean);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return mean;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return variance;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("KDistribution", v, w);
  }
}
