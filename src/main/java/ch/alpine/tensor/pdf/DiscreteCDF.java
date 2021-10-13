// code by jph
package ch.alpine.tensor.pdf;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** class performs the integration of probabilities to calculate the cumulative distribution function
 * whenever there is no closed form expression for the terms. */
/* package */ class DiscreteCDF implements CDF {
  private final DiscreteDistribution discreteDistribution;
  private final NavigableMap<Scalar, Scalar> cdf = new TreeMap<>();
  private boolean cdf_finished = false;

  public DiscreteCDF(DiscreteDistribution discreteDistribution) {
    this.discreteDistribution = discreteDistribution;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return p_function(x, scalar -> cdf.lowerEntry(scalar));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_function(x, scalar -> cdf.floorEntry(scalar));
  }

  // helper function
  private Scalar p_function(Scalar x, Function<Scalar, Entry<Scalar, Scalar>> function) {
    if (cdf.isEmpty()) {
      int first = discreteDistribution.lowerBound();
      cdf.put(RealScalar.of(first), discreteDistribution.p_equals(first));
    }
    Entry<Scalar, Scalar> ceiling = cdf.ceilingEntry(x);
    if (cdf_finished() || Objects.nonNull(ceiling)) {
      Entry<Scalar, Scalar> entry = function.apply(x);
      return Objects.isNull(entry) //
          ? RealScalar.ZERO
          : entry.getValue();
    }
    // <- ceiling == null, now integrate until finished or ceiling of x exists
    Entry<Scalar, Scalar> last = cdf.lastEntry();
    int k = Scalars.intValueExact(last.getKey());
    Scalar cumprob = last.getValue();
    while (Scalars.lessEquals(RealScalar.of(k), x) && !cdf_finished()) {
      ++k;
      Scalar p_equals = discreteDistribution.p_equals(k);
      cumprob = cumprob.add(p_equals);
      cdf.put(RealScalar.of(k), cumprob);
      cdf_finished = StaticHelper.isFinished(p_equals, cumprob);
    }
    return p_function(x, function);
  }

  /** @return whether built of cdf table has terminated */
  public boolean cdf_finished() {
    return cdf_finished;
  }
}
