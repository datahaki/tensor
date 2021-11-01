// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** functionality and suggested base class for a discrete probability distribution
 * 
 * <p>implementing classes are required to invoke
 * {@link #inverse_cdf_build(int)}, or
 * {@link #inverse_cdf_build(Chop)} in the constructor
 * 
 * @see BinomialDistribution
 * @see PoissonDistribution
 * @see PascalDistribution */
public abstract class EvaluatedDiscreteDistribution extends AbstractDiscreteDistribution implements Serializable {
  private static final Scalar _1 = DoubleScalar.of(1);
  // ---
  /** inverse cdf maps from probability to integers and is built during random sampling generation.
   * the value type of the map is Scalar (instead of Integer) to reuse the instances of Scalar */
  private final NavigableMap<Scalar, Scalar> inverse_cdf = new TreeMap<>();

  /** precomputes a lookup map for random variate generation via {@link #quantile(Scalar)}
   * safeguard when computing CDF for probabilities with machine precision
   * 
   * @param upperBound greatest integer n for which 0 < p(n), i.e. upper bound is inclusive */
  protected final void inverse_cdf_build(int upperBound) {
    Scalar cumprob = RealScalar.ZERO;
    for (int sample = lowerBound(); sample < upperBound; ++sample) {
      Scalar prob = p_equals(sample);
      if (Scalars.nonZero(prob)) {
        cumprob = cumprob.add(prob);
        inverse_cdf.put(cumprob, RealScalar.of(sample));
        if (Scalars.lessEquals(RealScalar.ONE, cumprob))
          return;
      }
    }
    inverse_cdf.put(RealScalar.ONE, RealScalar.of(upperBound));
  }

  /** precomputes a lookup map and determines numeric upper bound
   * 
   * @param chop */
  protected final void inverse_cdf_build(Chop chop) {
    int upperBound = lowerBound();
    Scalar cumprob = RealScalar.ZERO;
    while (true) {
      Scalar prob = p_equals(upperBound);
      if (Scalars.nonZero(prob)) {
        cumprob = cumprob.add(prob);
        inverse_cdf.put(cumprob, RealScalar.of(upperBound));
        if (chop.isClose(_1, cumprob))
          break;
      }
      ++upperBound;
    }
  }

  @Override // from InverseCDF
  public final Scalar quantile(Scalar p) {
    return inverse_cdf.ceilingEntry(Sign.requirePositiveOrZero(p)).getValue();
  }

  @Override // from AbstractDiscreteDistribution
  protected final Scalar protected_quantile(Scalar p) {
    return inverse_cdf.higherEntry(p).getValue();
  }

  /** @return */
  @PackageTestAccess
  final NavigableMap<Scalar, Scalar> inverse_cdf() {
    return Collections.unmodifiableNavigableMap(inverse_cdf);
  }
}
