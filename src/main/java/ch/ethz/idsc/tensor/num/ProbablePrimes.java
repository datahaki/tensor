// code by jph
package ch.ethz.idsc.tensor.num;

import java.math.BigInteger;
import java.util.Map;

import ch.ethz.idsc.tensor.qty.LruCache;

/* package */ enum ProbablePrimes {
  INSTANCE;

  /** Quote from BigInteger:
   * "certainty a measure of the uncertainty that the caller is
   * willing to tolerate: if the call returns {@code true}
   * the probability that this BigInteger is prime exceeds
   * (1 - 1/2<sup>{@code certainty}</sup>). The execution time of
   * this method is proportional to the value of this parameter." */
  private static final int CERTAINTY = 20;
  private static final int MAX_SIZE = 768;
  // ---
  private final Map<BigInteger, BigInteger> map = new LruCache<>(MAX_SIZE);

  /** @param bigInteger
   * @return bigInteger
   * @throws Exception if given bigInteger is not a prime */
  public synchronized BigInteger require(BigInteger bigInteger) {
    if (!map.containsKey(bigInteger)) {
      if (!bigInteger.isProbablePrime(CERTAINTY))
        throw new IllegalArgumentException("not prime: " + bigInteger);
      map.put(bigInteger, bigInteger);
    }
    return bigInteger;
  }
}
