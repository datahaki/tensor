// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LogisticDistribution.html">LogisticDistribution</a> */
public class LogisticDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param a
   * @param b positive
   * @return */
  public static Distribution of(Scalar a, Scalar b) {
    return new LogisticDistribution(Objects.requireNonNull(a), Sign.requirePositive(b));
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

  private LogisticDistribution(Scalar a, Scalar b) {
    this.a = a;
    this.b = b;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar exp = Exp.FUNCTION.apply(a.subtract(x).divide(b));
    Scalar sum = RealScalar.ONE.add(exp);
    return exp.divide(b).divide(sum.multiply(sum));
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return RealScalar.ONE.add(Exp.FUNCTION.apply(a.subtract(x).divide(b))).reciprocal();
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    return Scalars.isZero(p) //
        ? Quantity.of(DoubleScalar.NEGATIVE_INFINITY, QuantityUnit.of(a))
        : a.subtract(Log.FUNCTION.apply(p.reciprocal().subtract(RealScalar.ONE)).multiply(b));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return a;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    Scalar bpi = Pi.VALUE.multiply(b);
    return bpi.multiply(bpi).divide(RealScalar.of(3));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s]", getClass().getSimpleName(), a, b);
  }
}
