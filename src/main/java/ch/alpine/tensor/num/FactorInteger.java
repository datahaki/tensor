// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/** Pollard's rho
 * 
 * Reference:
 * CLRS
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FactorInteger.html">FactorInteger</a> */
public class FactorInteger {
  private static final Random RANDOM = new SecureRandom();

  /** @param n non-negative
   * @return */
  public static Map<BigInteger, Integer> of(BigInteger n) {
    if (n.signum() == -1)
      throw new IllegalArgumentException();
    return new FactorInteger(n).map;
  }

  /***************************************************/
  private final Map<BigInteger, Integer> map = new TreeMap<>();

  private FactorInteger(BigInteger n) {
    if (n.equals(BigInteger.ZERO))
      map.put(BigInteger.ZERO, 1);
    else //
    if (n.equals(BigInteger.ONE))
      map.put(BigInteger.ONE, 1);
    else
      recur(n);
  }

  private void recur(BigInteger n) {
    if (PrimeQ.of(n))
      map.merge(n, 1, Math::addExact);
    else {
      BigInteger d = divisor(n);
      recur(d);
      n = n.divide(d);
      if (BigInteger.ONE.compareTo(n) == -1) // is 1 < n ?
        recur(n);
    }
  }

  private static BigInteger random(BigInteger bigInteger) {
    return new BigInteger(bigInteger.bitLength(), RANDOM).mod(bigInteger.subtract(BigInteger.ONE));
  }

  // LONGTERM improve cycle detection
  private static BigInteger divisor(BigInteger n) {
    int i = 1;
    BigInteger xi = random(n);
    BigInteger y = xi;
    int k = 2;
    while (true) {
      i = i + 1;
      xi = xi.multiply(xi).add(RANDOM.nextBoolean() ? BigInteger.ONE : BigInteger.ONE.negate()).mod(n);
      BigInteger d = y.subtract(xi).gcd(n);
      if (!d.equals(BigInteger.ONE) && !d.equals(n))
        return d;
      if (i == k) {
        y = xi;
        k = 2 * k;
        if (BigInteger.valueOf(k).compareTo(n) == +1)
          xi = random(n);
      }
    }
  }
}
