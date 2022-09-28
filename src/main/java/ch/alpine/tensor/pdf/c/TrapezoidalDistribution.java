// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Characteristics of a trapezoidal distribution: the graph of the PDF resembles
 * a trapezoid which begins rising at a until b, has a plateau from b to c, and
 * then falls after c to point d.
 * 
 * <p>A special case is a triangular distribution where b == c. In that case,
 * the plateau has width zero.
 * 
 * <p>inspired by
 * <a href="https://en.wikipedia.org/wiki/Trapezoidal_distribution">TrapezoidalDistribution</a> */
public class TrapezoidalDistribution extends AbstractContinuousDistribution //
    implements CentralMomentInterface, Serializable {
  /** @param a
   * @param b
   * @param c
   * @param d
   * @return distribution with support in the interval [a, d]
   * @throws Exception unless a <= b <= c <= d and a < d */
  public static Distribution of(Scalar a, Scalar b, Scalar c, Scalar d) {
    if (Scalars.lessThan(a, d) && Scalars.lessEquals(b, c))
      return new TrapezoidalDistribution(a, b, c, d);
    throw new Throw(a, b, c, d);
  }

  /** @param a
   * @param b
   * @param c
   * @param d
   * @return distribution with support in the interval [a, d]
   * @throws Exception unless a <= b <= c <= d and a < d */
  public static Distribution of(Number a, Number b, Number c, Number d) {
    return of(RealScalar.of(a), RealScalar.of(b), RealScalar.of(c), RealScalar.of(d));
  }

  /** @param mean
   * @param sigma
   * @param spread between sqrt(3) and sqrt(6), i.e. in the interval [1.73205..., 2.44949...]
   * @return distribution with support in the interval
   * [mean - sigma * spread, mean + sigma * spread]
   * and variance of sigma ^ 2 */
  public static Distribution with(Scalar mean, Scalar sigma, Scalar spread) {
    Scalar f1 = Sqrt.FUNCTION.apply(RealScalar.of(6).subtract(spread.multiply(spread)));
    Scalar d1 = sigma.multiply(f1);
    Scalar d2 = sigma.multiply(spread);
    return of(mean.subtract(d2), mean.subtract(d1), mean.add(d1), mean.add(d2));
  }

  /** @param mean
   * @param sigma
   * @param spread between sqrt(3) and sqrt(6), i.e. in the interval [1.73205..., 2.44949...]
   * @return distribution with support in the interval
   * [mean - sigma * spread, mean + sigma * spread]
   * and variance of sigma ^ 2 */
  public static Distribution with(Number mean, Number sigma, Number spread) {
    return with(RealScalar.of(mean), RealScalar.of(sigma), RealScalar.of(spread));
  }

  // ---
  private final Scalar a;
  private final Scalar b;
  private final Scalar c;
  private final Scalar d;
  private final TrapezoidalDistribution0 trapezoidalDistribution0;

  public TrapezoidalDistribution(Scalar a, Scalar b, Scalar c, Scalar d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    trapezoidalDistribution0 = new TrapezoidalDistribution0( //
        a.subtract(a), //
        b.subtract(a), //
        c.subtract(a), //
        d.subtract(a));
  }

  @Override
  public Scalar at(Scalar x) {
    return trapezoidalDistribution0.at(x.subtract(a));
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    return trapezoidalDistribution0.p_lessThan(x.subtract(a));
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    return trapezoidalDistribution0.protected_quantile(p).add(a);
  }

  @Override
  public Scalar mean() {
    return trapezoidalDistribution0.mean().add(a);
  }

  @Override
  public Scalar variance() {
    return trapezoidalDistribution0.variance();
  }

  @Override
  public Scalar centralMoment(int order) {
    return trapezoidalDistribution0.centralMoment(order);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("TrapezoidalDistribution", a, b, c, d);
  }
}
