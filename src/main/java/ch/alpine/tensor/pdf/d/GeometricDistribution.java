// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;
import java.math.BigInteger;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Power;

/** Careful:
 * if parameter p is in exact precision, the probabilities may evaluate
 * to long exact fractions and slow down the computation.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/GeometricDistribution.html">GeometricDistribution</a> */
public class GeometricDistribution extends AbstractDiscreteDistribution implements Serializable {
  /** @param p with 0 < p <= 1 denotes probability P(X==0) == p
   * @return */
  public static Distribution of(Scalar p) {
    if (p.equals(RealScalar.ONE))
      return BinomialDistribution.of(0, p);
    if (Scalars.lessThan(RealScalar.ZERO, p) && //
        Scalars.lessEquals(p, RealScalar.ONE))
      return new GeometricDistribution(p);
    throw new Throw(p);
  }

  /** @param p with 0 < p <= 1 denotes probability P(X==0) == p
   * @return */
  public static Distribution of(Number p) {
    return of(RealScalar.of(p));
  }

  // ---
  private final Scalar p;
  private final Scalar _1_p; // _1_p == 1 - p

  private GeometricDistribution(Scalar p) {
    this.p = p;
    this._1_p = RealScalar.ONE.subtract(p);
  }

  @Override // from UnivariateDistribution
  public Clip support() {
    return Clips.positive(DoubleScalar.POSITIVE_INFINITY);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return p.reciprocal().subtract(RealScalar.ONE);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return _1_p.divide(p.multiply(p));
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return protected_quantile(Clips.unit().requireInside(p));
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_quantile(Scalar p) { // p shadows member, ok
    Scalar num = Log.FUNCTION.apply(RealScalar.ONE.subtract(p));
    Scalar den = Log.FUNCTION.apply(_1_p);
    return Floor.FUNCTION.apply(num.divide(den));
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger x) {
    // PDF[GeometricDistribution[p], x] == (1 - p) ^ x * p
    return p.multiply(Power.of(_1_p, x));
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Scalars.lessThan(RealScalar.ZERO, x) //
        ? RealScalar.ONE.subtract(Power.of(_1_p, Ceiling.FUNCTION.apply(x)))
        : RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return Scalars.lessEquals(RealScalar.ZERO, x) //
        ? RealScalar.ONE.subtract(Power.of(_1_p, RealScalar.ONE.add(Floor.FUNCTION.apply(x))))
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("GeometricDistribution", p);
  }
}
