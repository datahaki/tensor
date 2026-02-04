// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.PrimeQ;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomPrime.html">RandomPrime</a> */
public class RandomPrime implements Distribution, Serializable {
  /** @param min inclusive
   * @param max inclusive
   * @return */
  public static final Distribution of(Scalar min, Scalar max) {
    return new RandomPrime(min, max);
  }

  /** @param min inclusive
   * @param max inclusive
   * @return */
  public static final Distribution of(int min, int max) {
    return of(RealScalar.of(min), RealScalar.of(max));
  }

  // ---
  private final Distribution distribution;
  private final Scalar width;

  private RandomPrime(Scalar min, Scalar max) {
    distribution = DiscreteUniformDistribution.of(min, max);
    width = max.subtract(min);
  }

  @Override
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    Scalar count = width.zero();
    while (Scalars.lessEquals(count, width)) { // TODO not a good choice
      Scalar scalar = RandomVariate.of(distribution, randomGenerator);
      if (PrimeQ.of(scalar))
        return scalar;
      count = count.add(count.one());
    }
    // there is a real chance that no primes exist in the given interval
    throw new Throw(distribution);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("RandomPrime", distribution);
  }
}
