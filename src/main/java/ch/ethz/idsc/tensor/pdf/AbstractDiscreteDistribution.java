// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** functionality for a discrete probability distribution */
public abstract class AbstractDiscreteDistribution implements DiscreteDistribution {
  // inverse cdf is built during random sampling generation
  // inverse cdf maps from probability to sample
  private final NavigableMap<Scalar, Scalar> inverse_cdf = new TreeMap<>();

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    if (inverse_cdf.isEmpty()) {
      int sample = lowerBound();
      inverse_cdf.put(p_equals(sample), RealScalar.of(sample));
    }
    Scalar reference = RealScalar.of(random.nextDouble());
    Entry<Scalar, Scalar> higher = inverse_cdf.higherEntry(reference); // strictly higher than cdf
    if (higher == null) {
      Entry<Scalar, Scalar> lower = inverse_cdf.floorEntry(reference); // less than or equal
      int sample = lower.getValue().number().intValue();
      Scalar cumprob = lower.getKey();
      while (Scalars.lessThan(cumprob, reference)) {
        ++sample;
        cumprob = cumprob.add(p_equals(sample));
        inverse_cdf.put(cumprob, RealScalar.of(sample));
      }
    }
    return inverse_cdf.higherEntry(reference).getValue();
  }
}
