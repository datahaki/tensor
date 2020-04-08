// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LogisticDistribution.html">LogisticDistribution</a> */
public class LogisticDistribution extends AbstractContinuousDistribution //
    implements InverseCDF, MeanInterface, VarianceInterface, Serializable {
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

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return a.subtract(Log.FUNCTION.apply(p.reciprocal().subtract(RealScalar.ONE)).multiply(b));
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar randomVariate(double reference) {
    return quantile(DoubleScalar.of(reference));
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
