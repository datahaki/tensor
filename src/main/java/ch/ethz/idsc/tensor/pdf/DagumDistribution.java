// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DagumDistribution.html">DagumDistribution</a> */
public class DagumDistribution extends AbstractContinuousDistribution implements //
    InverseCDF, Serializable {
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

  /***************************************************/
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
  private final Scalar power_pan_b;
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
    power_pan_b = Power.function(pa.negate()).apply(b);
    power_pa1 = Power.function(pa.subtract(RealScalar.ONE));
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return power_pn.apply(power_an.apply(x.divide(b)).add(RealScalar.ONE));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Times.of( //
        pa, power_pan_b, power_pa1.apply(x), //
        power_p1n.apply(power_a.apply(x.divide(b)).add(RealScalar.ONE)) //
    );
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return _quantile(Clips.unit().requireInside(p));
  }

  private Scalar _quantile(Scalar p) {
    return Scalars.isZero(p) //
        ? b.zero()
        : power_arn.apply(power_prn.apply(p).subtract(RealScalar.ONE)).multiply(b);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar randomVariate(double reference) {
    return _quantile(DoubleScalar.of(reference));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s, %s]", getClass().getSimpleName(), p, a, b);
  }
}
