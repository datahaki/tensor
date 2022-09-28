// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.sca.gam.Beta;
import ch.alpine.tensor.sca.pow.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BetaDistribution.html">BetaDistribution</a> */
public class BetaDistribution implements Distribution, MeanInterface, PDF, VarianceInterface, Serializable {
  /** Remark:
   * for a1 == 1 OR a2 == 1 the distribution does not require the beta function
   * 
   * @param a1 strictly positive real scalar
   * @param a2 strictly positive real scalar
   * @return */
  public static Distribution of(Scalar a1, Scalar a2) {
    if (Scalars.lessThan(RealScalar.ZERO, a1) && //
        Scalars.lessThan(RealScalar.ZERO, a2))
      return new BetaDistribution(a1, a2);
    throw new Throw(a1, a2);
  }

  /** @param a1 positive
   * @param a2 positive
   * @return */
  public static Distribution of(Number a1, Number a2) {
    return of(RealScalar.of(a1), RealScalar.of(a2));
  }

  // ---
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

  // CDF requires BetaRegularized
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
