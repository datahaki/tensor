// code by jph
package ch.alpine.tensor.pdf.c;

import java.util.OptionalInt;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;

/** Quote: "If x has the standard N(0, 1) distribution, then x^2 has a chi-squared distribution."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ChiSquareDistribution.html">ChiSquareDistribution</a> */
public class ChiSquareDistribution extends GammaDistribution {
  private static final Scalar TWO = RealScalar.TWO;
  private static final int LIMIT = 10;

  /** @param nu positive real
   * @return
   * @throws Exception if nu is not positive or not an instance of {@link RealScalar} */
  public static Distribution of(Scalar nu) {
    if (Scalars.lessThan(RealScalar.ZERO, nu)) {
      OptionalInt optionalInt = Scalars.optionalInt(nu);
      if (optionalInt.isPresent()) {
        int n = optionalInt.orElseThrow();
        if (n <= LIMIT)
          return new ChiSquareDistribution(n);
      }
      return GammaDistribution.of(nu.divide(TWO), TWO);
    }
    throw new Throw(nu);
  }

  /** @param nu positive real
   * @return */
  public static Distribution of(Number nu) {
    return of(RealScalar.of(nu));
  }

  // ---
  private final int n;

  private ChiSquareDistribution(int n) {
    super(Rational.of(n, 2), TWO);
    this.n = n;
  }

  // CDF requires GammaRegularized
  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return Vector2NormSquared.of(RandomVariate.stream(NormalDistribution.standard(), randomGenerator) //
        .limit(n));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ChiSquareDistribution", n);
  }
}
