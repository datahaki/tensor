// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;
import java.util.function.Function;

import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Cache;

/** Careful: Implementation only asserts primality with a certain probability.
 *
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PrimeQ.html">PrimeQ</a> */
public enum PrimeQ {
  ;
  /** Quote from BigInteger:
   * "certainty a measure of the uncertainty that the caller is
   * willing to tolerate: if the call returns {@code true}
   * the probability that this BigInteger is prime exceeds
   * (1 - 1/2<sup>{@code certainty}</sup>). The execution time of
   * this method is proportional to the value of this parameter." */
  private static final int CERTAINTY = 20;
  private static final int MAX_SIZE = 768;
  private static final Function<BigInteger, BigInteger> CACHE = Cache.of(PrimeQ::register, MAX_SIZE);

  /** @param bigInteger
   * @return whether given bigInteger is probably prime */
  public static boolean of(BigInteger bigInteger) {
    return bigInteger.isProbablePrime(CERTAINTY);
  }

  /** @param scalar
   * @return
   * @throws Exception if given scalars does not satisfy {@link IntegerQ} */
  public static boolean of(Scalar scalar) {
    return of(Scalars.bigIntegerValueExact(scalar));
  }

  /** @param bigInteger
   * @return
   * @throws Exception if given bigInteger is not prime */
  public static BigInteger require(BigInteger bigInteger) {
    return CACHE.apply(bigInteger);
  }

  /** @param scalar
   * @return
   * @throws Exception if given bigInteger is not prime
   * @throws Exception if given scalars does not satisfy {@link IntegerQ} */
  public static Scalar require(Scalar scalar) {
    return RealScalar.of(require(Scalars.bigIntegerValueExact(scalar)));
  }

  /** @param bigInteger
   * @return bigInteger
   * @throws Exception if given bigInteger is not a prime */
  private static BigInteger register(BigInteger bigInteger) {
    if (of(bigInteger))
      return bigInteger;
    throw new IllegalArgumentException("not prime: " + bigInteger);
  }
}
