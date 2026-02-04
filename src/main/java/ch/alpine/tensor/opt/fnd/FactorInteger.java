// code by jph
package ch.alpine.tensor.opt.fnd;

import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.PrimeQ;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

/** Pollard's rho
 * 
 * Reference:
 * CLRS
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FactorInteger.html">FactorInteger</a> */
public class FactorInteger {
  /** @param scalar
   * @return */
  public static Map<Scalar, Integer> of(Scalar scalar) {
    return of(Scalars.bigIntegerValueExact(scalar)).entrySet().stream().collect(Collectors.toMap(e -> RealScalar.of(e.getKey()), Entry::getValue));
  }

  /** @param n non-negative
   * @return */
  public static Map<BigInteger, Integer> of(BigInteger n) {
    if (n.signum() == -1)
      throw new IllegalArgumentException();
    return new FactorInteger(n).map;
  }

  // ---
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
      BigInteger d = factor(n);
      recur(d);
      n = n.divide(d);
      if (BigInteger.ONE.compareTo(n) < 0) // is 1 < n ?
        recur(n);
    }
  }

  private static final BigInteger TWO = BigInteger.valueOf(2);

  /* package */ static BigInteger factor(BigInteger n) {
    if (!n.testBit(0))
      return TWO;
    if (PrimeQ.of(n))
      return n;
    Distribution distribution = DiscreteUniformDistribution.forArray(n);
    while (true) {
      BigInteger x = Scalars.bigIntegerValueExact(RandomVariate.of(distribution));
      BigInteger y = x;
      BigInteger c = Scalars.bigIntegerValueExact(RandomVariate.of(distribution));
      BigInteger d = BigInteger.ONE;
      while (d.equals(BigInteger.ONE)) {
        // Tortoise move: x = f(x)
        x = x.multiply(x).add(c).remainder(n);
        // Hare move: y = f(f(y))
        y = y.multiply(y).add(c).remainder(n);
        y = y.multiply(y).add(c).remainder(n);
        // Check GCD of the difference
        d = x.subtract(y).abs().gcd(n);
      }
      // If d == n, the algorithm failed to find a factor with this 'c'
      if (!d.equals(n))
        return d;
    }
  }
}
