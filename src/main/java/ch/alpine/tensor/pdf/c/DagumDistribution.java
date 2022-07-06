// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.gam.LogGamma;
import ch.alpine.tensor.sca.pow.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DagumDistribution.html">DagumDistribution</a> */
public class DagumDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param p positive
   * @param a positive
   * @param b positive
   * @return */
  public static Distribution of(Scalar p, Scalar a, Scalar b) {
    return new DagumDistribution( //
        Sign.requirePositive(p), //
        Sign.requirePositive(a), //
        Sign.requirePositive(b));
  }

  /** @param p positive
   * @param a positive
   * @param b positive
   * @return */
  public static Distribution of(Number p, Number a, Number b) {
    return of( //
        RealScalar.of(p), //
        RealScalar.of(a), //
        RealScalar.of(b));
  }

  // ---
  private final Scalar p;
  private final Scalar a;
  private final Scalar b;
  private final Scalar pa;
  private final ScalarUnaryOperator power_a;
  private final ScalarUnaryOperator power_prn;
  private final ScalarUnaryOperator power_p1n;
  private final ScalarUnaryOperator power_arn;
  private final ScalarUnaryOperator power_pn;
  private final ScalarUnaryOperator power_an;
  private final Scalar pa_power_pan_b;
  private final ScalarUnaryOperator power_pa1;

  private DagumDistribution(Scalar p, Scalar a, Scalar b) {
    this.p = p;
    this.a = a;
    this.b = b;
    power_a = Power.function(a);
    power_pn = Power.function(p.negate());
    power_p1n = Power.function(p.add(RealScalar.ONE).negate());
    power_an = Power.function(a.negate());
    power_prn = Power.function(p.reciprocal().negate());
    power_arn = Power.function(a.reciprocal().negate());
    pa = p.multiply(a);
    pa_power_pan_b = Power.of(b, pa.negate()).multiply(pa); // pa * b^(-pa)
    power_pa1 = Power.function(pa.subtract(RealScalar.ONE));
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return power_pn.apply(power_an.apply(x.divide(b)).add(RealScalar.ONE));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Times.of( //
        pa_power_pan_b, power_pa1.apply(x), //
        power_p1n.apply(power_a.apply(x.divide(b)).add(RealScalar.ONE)) //
    );
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    return Scalars.isZero(p) //
        ? b.zero()
        : power_arn.apply(power_prn.apply(p).subtract(RealScalar.ONE)).multiply(b);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    if (Scalars.lessThan(RealScalar.ONE, a)) {
      Scalar f1 = LogGamma.FUNCTION.apply(a.subtract(RealScalar.ONE).divide(a));
      Scalar f2 = LogGamma.FUNCTION.apply(a.reciprocal().add(p));
      Scalar f3 = LogGamma.FUNCTION.apply(p);
      return Exp.FUNCTION.apply(f1.add(f2).subtract(f3)).multiply(b);
    }
    return DoubleScalar.INDETERMINATE;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    throw new UnsupportedOperationException();
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("DagumDistribution", p, a, b);
  }
}
