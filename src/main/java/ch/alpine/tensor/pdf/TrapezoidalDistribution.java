// code by clruch
package ch.alpine.tensor.pdf;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.itp.Fit;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sqrt;

/** Characteristics of a trapezoidal distribution: the graph of the PDF resembles
 * a trapezoid which begins rising at a until b, has a plateau from b to c, and
 * then falls after c to point d.
 * 
 * <p>A special case is a triangular distribution where b == c. In that case,
 * the plateau has width zero.
 * 
 * <p>inspired by
 * <a href="https://en.wikipedia.org/wiki/Trapezoidal_distribution">TrapezoidalDistribution</a> */
public class TrapezoidalDistribution extends AbstractContinuousDistribution implements Serializable {
  private static final Scalar _1_3 = RationalScalar.of(1, 3);

  /** @param a
   * @param b
   * @param c
   * @param d
   * @return distribution with support in the interval [a, d]
   * @throws Exception unless a <= b <= c <= d and a < d */
  public static Distribution of(Scalar a, Scalar b, Scalar c, Scalar d) {
    if (Scalars.lessThan(c, b) || Scalars.lessEquals(d, a))
      throw TensorRuntimeException.of(b, c);
    return new TrapezoidalDistribution(a, b, c, d);
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
   * @param spread
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
   * @param spread
   * @return distribution with support in the interval
   * [mean - sigma * spread, mean + sigma * spread]
   * and variance of sigma ^ 2 */
  public static Distribution with(Number mean, Number sigma, Number spread) {
    return with(RealScalar.of(mean), RealScalar.of(sigma), RealScalar.of(spread));
  }

  // ---
  private final Clip clip;
  private final Scalar a;
  private final Scalar b;
  private final Scalar c;
  private final Scalar d;
  private final Scalar alpha_inv;
  private final Scalar alpha;
  private final Scalar yB;
  private final Scalar yC;

  private TrapezoidalDistribution(Scalar a, Scalar b, Scalar c, Scalar d) {
    clip = Clips.interval(a, d);
    this.a = a;
    this.b = clip.requireInside(b);
    this.c = clip.requireInside(c);
    this.d = d;
    alpha_inv = d.add(c).subtract(a).subtract(b);
    this.alpha = alpha_inv.reciprocal();
    yB = p_lessThan(b);
    yC = p_lessThan(c);
  }

  /** @return support of distribution */
  public Clip support() {
    return Clips.interval(a, d);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar value = _p_lessThan(x);
    return x.one().multiply(value);
  }

  private Scalar _p_lessThan(Scalar x) {
    if (Scalars.lessThan(x, a))
      return RealScalar.ZERO;
    if (Scalars.lessThan(x, b)) {
      Scalar term1 = RealScalar.ONE.divide(b.subtract(a));
      Scalar term2 = x.subtract(a).multiply(x.subtract(a));
      return alpha.multiply(term1).multiply(term2);
    }
    if (Scalars.lessEquals(x, c)) {
      Scalar term2 = x.add(x).subtract(a).subtract(b);
      return alpha.multiply(term2);
    }
    if (Scalars.lessThan(x, d)) {
      Scalar term1 = RealScalar.ONE.divide(d.subtract(c));
      Scalar term2 = d.subtract(x).multiply(d.subtract(x));
      return RealScalar.ONE.subtract(alpha.multiply(term1).multiply(term2));
    }
    return RealScalar.ONE;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar value = _at(x);
    return x.one().multiply(value);
  }

  private Scalar _at(Scalar x) {
    if (clip.isInside(x)) { // support is [a, d]
      Scalar two_alpha = alpha.add(alpha);
      if (Scalars.lessThan(x, b)) {
        Scalar term = x.subtract(a).divide(b.subtract(a));
        return two_alpha.multiply(term);
      }
      if (Scalars.lessEquals(x, c))
        return two_alpha;
      // here is case c < x <= d
      Scalar term = d.subtract(x).divide(d.subtract(c));
      return two_alpha.multiply(term);
    }
    return RealScalar.ZERO;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    Scalar cd = c.multiply(c).add(c.multiply(d)).add(d.multiply(d));
    Scalar ab = a.multiply(a).add(a.multiply(b)).add(b.multiply(b));
    return alpha.multiply(cd.subtract(ab)).multiply(_1_3);
    // TODO for some reason cannot be substituted
    // ScalarUnaryOperator n_mean = s->s;
    // Tensor x2 = UnitVector.of(2, 1);
    // return contrib(a, b, n_mean, x2).add(contrib(b, c, n_mean, x2)).add(contrib(c, d, n_mean, x2));
  }

  private Scalar contrib(Scalar lo, Scalar hi, ScalarUnaryOperator map, Polynomial x2) {
    // System.out.println(lo);
    // System.out.println(hi);
    // TODO check with Quantity
    if (lo.equals(hi)) {
      Tensor dab = Tensors.of(lo).map(map);
      // System.out.println(dab);
      Polynomial _ab = Fit.polynomial(dab, Tensors.of(lo).map(this::at), 0);
      System.out.println(_ab.coeffs());
      Polynomial iab = _ab.product(x2).integral();
      return dab.map(iab).Get(0).zero();
    }
    Tensor dab = Tensors.of(lo, hi).map(map);
    // System.out.println(dab);
    Polynomial _ab = Fit.polynomial(dab, Tensors.of(lo, hi).map(this::at), 1);
    System.out.println(_ab.coeffs());
    // System.out.println(_ab);
    // System.out.println(x2);
    Polynomial iab = _ab.product(x2).integral();
    // System.out.println(dab.map(iab));
    return Differences.of(dab.map(iab)).Get(0);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    ScalarUnaryOperator n_mean = mean().negate()::add;
    Polynomial x2 = Polynomial.of(Tensors.fromString("{0[m^-1],0[m^-2],1[m^-3]}"));
    return contrib(a, b, n_mean, x2).add(contrib(b, c, n_mean, x2)).add(contrib(c, d, n_mean, x2));
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    if (Scalars.lessEquals(p, yB)) // y <= yB
      return Sqrt.FUNCTION.apply(alpha_inv.multiply(b.subtract(a)).multiply(p)).add(a);
    // yB < y <= yC
    if (Scalars.lessEquals(p, yC))
      return p.multiply(alpha_inv).add(a).add(b).multiply(RationalScalar.HALF);
    // yC < y
    return d.subtract(Sqrt.FUNCTION.apply( //
        RealScalar.ONE.subtract(p).multiply(alpha_inv).multiply(d.subtract(c))));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s, %s, %s]", getClass().getSimpleName(), a, b, c, d);
  }
}
