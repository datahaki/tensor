// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;

/** functionality and suggested base class for a discrete probability distribution
 * 
 * <p>implementing classes are required to invoke
 * {@link #build(int)}, or
 * {@link #build(Chop)} in the constructor
 * 
 * @see BinomialDistribution
 * @see CategoricalDistribution
 * @see HypergeometricDistribution
 * @see NegativeBinomialDistribution
 * @see PascalDistribution
 * @see PoissonDistribution */
public abstract class EvaluatedDiscreteDistribution extends AbstractDiscreteDistribution //
    implements CentralMomentInterface, Serializable {
  private static final Scalar _1 = DoubleScalar.of(1);
  // ---
  private final NavigableMap<Scalar, Scalar> cdf = new TreeMap<>();
  /** inverse cdf maps from probability to integers and is built during random sampling generation.
   * the value type of the map is Scalar (instead of Integer) to reuse the instances of Scalar */
  private final NavigableMap<Scalar, Scalar> inverse_cdf = new TreeMap<>();

  /** precomputes a lookup map for random variate generation via {@link #quantile(Scalar)}
   * safeguard when computing CDF for probabilities with machine precision
   * 
   * @param upperBound greatest integer n for which 0 < p(n), i.e. upper bound is inclusive */
  protected final void build(int upperBound) {
    Scalar cumprob = RealScalar.ZERO;
    for (int sample = lowerBound().intValueExact(); sample < upperBound; ++sample) {
      Scalar prob = p_equals(BigInteger.valueOf(sample));
      if (Scalars.nonZero(prob)) {
        cumprob = cumprob.add(prob);
        Scalar x = RealScalar.of(sample);
        cdf.put(x, cumprob);
        inverse_cdf.put(cumprob, x);
        if (Scalars.lessEquals(RealScalar.ONE, cumprob))
          return;
      }
    }
    Scalar x = RealScalar.of(upperBound);
    cdf.put(x, RealScalar.ONE);
    inverse_cdf.put(RealScalar.ONE, x);
  }

  /** precomputes a lookup map and determines numeric upper bound
   * 
   * @param chop */
  protected final void build(Chop chop) {
    BigInteger upperBound = lowerBound();
    Scalar cumprob = RealScalar.ZERO;
    while (true) {
      Scalar prob = p_equals(upperBound);
      if (Scalars.nonZero(prob)) {
        cumprob = cumprob.add(prob);
        Scalar x = RealScalar.of(upperBound);
        cdf.put(x, cumprob);
        inverse_cdf.put(cumprob, x);
        if (chop.isClose(_1, cumprob))
          break;
      }
      upperBound = upperBound.add(BigInteger.ONE);
    }
  }

  @Override // from CentralMomentInterface
  public final Scalar centralMoment(int order) {
    Scalar mean = mean();
    ScalarUnaryOperator power = Power.function(order);
    return inverse_cdf.values().stream() //
        .map(x -> power.apply(x.subtract(mean)).multiply(at(x))) //
        .reduce(Scalar::add) //
        .orElseThrow();
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

  @Override // from CDF
  public final Scalar p_lessThan(Scalar x) {
    Entry<Scalar, Scalar> entry = cdf.lowerEntry(x);
    return Objects.isNull(entry) //
        ? RealScalar.ZERO
        : entry.getValue();
  }

  @Override // from CDF
  public final Scalar p_lessEquals(Scalar x) {
    Entry<Scalar, Scalar> entry = cdf.floorEntry(x);
    return Objects.isNull(entry) //
        ? RealScalar.ZERO
        : entry.getValue();
  }
}
