// code by jph
package ch.ethz.idsc.tensor.num;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

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
  private final Map<BigInteger, BigInteger> map = //
      new LinkedHashMap<BigInteger, BigInteger>(MAX_SIZE * 4 / 3, 0.75f, true) {
        private static final long serialVersionUID = 485826539213596939L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<BigInteger, BigInteger> eldest) {
          return MAX_SIZE < size();
        }
      };

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
