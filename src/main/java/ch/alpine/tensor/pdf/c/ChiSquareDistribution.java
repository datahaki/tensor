// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariateInterface;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.LogGamma;
import ch.alpine.tensor.sca.pow.Power;

/** Quote: "If x has the standard N(0, 1) distribution, then x^2 has a chi-squared distribution."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ChiSquareDistribution.html">ChiSquareDistribution</a> */
public class ChiSquareDistribution implements Distribution, //
    PDF, RandomVariateInterface, MeanInterface, VarianceInterface, Serializable {
  /** @param nu positive real
   * @return
   * @throws Exception if nu is not positive or not an instance of {@link RealScalar} */
  public static Distribution of(Scalar nu) {
    if (Scalars.lessThan(RealScalar.ZERO, nu))
      return new ChiSquareDistribution(nu);
    throw new Throw(nu);
  }

  /** @param nu positive real
   * @return */
  public static Distribution of(Number nu) {
    return of(RealScalar.of(nu));
  }

  // ---
  private final Scalar nu;
  private final Scalar nu2;
  private final Scalar log;

  private ChiSquareDistribution(Scalar nu) {
    this.nu = nu;
    nu2 = nu.multiply(RationalScalar.HALF);
    log = Log.FUNCTION.apply(RealScalar.TWO).multiply(nu2).add(LogGamma.FUNCTION.apply(nu2));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x))
      return Exp.FUNCTION.apply(log.add(x.multiply(RationalScalar.HALF)).negate()) //
          .multiply(Power.of(x, nu2.subtract(RealScalar.ONE)));
    return RealScalar.ZERO;
  }

  // CDF requires GammaRegularized
  @Override // from RandomVariateInterface
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    throw new UnsupportedOperationException();
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return nu;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return nu.add(nu);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ChiSquareDistribution", nu);
  }
}
