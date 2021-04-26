// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Beta;
import ch.ethz.idsc.tensor.sca.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BetaDistribution.html">BetaDistribution</a>
 * 
 * @see DirichletDistribution */
public class BetaDistribution implements Distribution, MeanInterface, PDF, VarianceInterface, Serializable {
  /** @param a1 positive
   * @param a2 positive
   * @return */
  public static Distribution of(Scalar a1, Scalar a2) {
    if (Scalars.lessEquals(a1, RealScalar.ZERO) || //
        Scalars.lessEquals(a2, RealScalar.ZERO))
      throw TensorRuntimeException.of(a1, a2);
    // LONGTERM for a1 == 1 OR a2 == 1 the distribution does not require the beta function
    return new BetaDistribution(a1, a2);
  }

  /** @param a1 positive
   * @param a2 positive
   * @return */
  public static Distribution of(Number a1, Number a2) {
    return of(RealScalar.of(a1), RealScalar.of(a2));
  }

  /***************************************************/
  private final Scalar a1;
  private final Scalar a2;
  private final Scalar factor;
  private final ScalarUnaryOperator power1;
  private final ScalarUnaryOperator power2;

  private BetaDistribution(Scalar a1, Scalar a2) {
    this.a1 = a1;
    this.a2 = a2;
    power1 = Power.function(a1.subtract(RealScalar.ONE));
    power2 = Power.function(a2.subtract(RealScalar.ONE));
    factor = Beta.of(a1, a2);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Scalars.lessThan(RealScalar.ZERO, x) //
        && Scalars.lessThan(x, RealScalar.ONE) //
            ? power1.apply(x).multiply(power2.apply(RealScalar.ONE.subtract(x))).divide(factor)
            : RealScalar.ZERO;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return a1.divide(a1.add(a2));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    Scalar a12 = a1.add(a2);
    return a1.divide(a12).multiply(a2).divide(a12).divide(RealScalar.ONE.add(a12));
  }
}
