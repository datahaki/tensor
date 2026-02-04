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
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.bes.BesselI;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.ply.LaguerreL;
import ch.alpine.tensor.sca.pow.Sqrt;

/** CDF requires MarcumQ
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RiceDistribution.html">RiceDistribution</a> */
public class RiceDistribution implements Distribution, //
    PDF, MeanInterface, VarianceInterface, Serializable {
  /** @param alpha non-negative
   * @param beta strictly positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessEquals(RealScalar.ZERO, alpha) && //
        Scalars.lessThan(RealScalar.ZERO, beta))
      return new RiceDistribution(alpha, beta);
    throw new Throw(alpha, beta);
  }

  /** @param alpha non-negative
   * @param beta strictly positive
   * @return */
  public static Distribution of(Number alpha, Number beta) {
    return of(RealScalar.of(alpha), RealScalar.of(beta));
  }

  // ---
  private final Scalar alpha;
  private final Scalar beta;
  private final Scalar a2;
  private final Scalar b2;
  private final Distribution nd1;
  private final Distribution nd2;

  private RiceDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
    a2 = alpha.multiply(alpha);
    b2 = beta.multiply(beta);
    nd1 = NormalDistribution.of(alpha, beta);
    nd2 = NormalDistribution.of(alpha.zero(), beta);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar factor = Exp.FUNCTION.apply(x.multiply(x).add(a2).divide(b2).multiply(RationalScalar.HALF).negate());
      if (Scalars.nonZero(factor))
        return Times.of(factor, x, BesselI._0(x.multiply(alpha).divide(b2))).divide(b2);
    }
    return RealScalar.ZERO;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return Times.of( //
        Sqrt.FUNCTION.apply(Pi.HALF), //
        beta, //
        LaguerreL.of(RationalScalar.HALF, a2.divide(b2).multiply(RationalScalar.HALF).negate()));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    Scalar g = LaguerreL.of(RationalScalar.HALF, a2.divide(b2).multiply(RationalScalar.HALF).negate());
    Scalar f = Times.of(Pi.HALF, b2, g, g);
    return a2.add(b2).add(b2).subtract(f);
  }

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return Hypot.of( //
        nd1.randomVariate(randomGenerator), //
        nd2.randomVariate(randomGenerator));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("RiceDistribution", alpha, beta);
  }
}
