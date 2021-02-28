// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.Serializable;
import java.util.OptionalInt;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.sca.Gamma;

/** binomial coefficient implemented for integer input
 * <pre>
 * Gamma[n+1] / ( Gamma[m+1] Gamma[n-m+1] )
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Binomial.html">Binomial</a> */
public class Binomial implements Serializable {
  private static final int MAX_SIZE = 384;
  private static final Function<Integer, Binomial> CACHE = Cache.of(Binomial::new, MAX_SIZE);

  /** <code>Mathematica::Binomial[n, m]</code>
   * 
   * @param n
   * @param m
   * @return binomial coefficient defined by n and m */
  public static Scalar of(Scalar n, Scalar m) {
    OptionalInt _n = Scalars.optionalInt(n);
    OptionalInt _m = Scalars.optionalInt(m);
    if (_n.isPresent() && _m.isPresent())
      return of(_n.getAsInt(), _m.getAsInt());
    Scalar np1 = n.add(RealScalar.ONE);
    return Gamma.FUNCTION.apply(np1).divide( //
        Gamma.FUNCTION.apply(m.add(RealScalar.ONE)).multiply(Gamma.FUNCTION.apply(np1.subtract(m))));
  }

  /** <code>Mathematica::Binomial[n, m]</code>
   * 
   * @param n
   * @param m
   * @return binomial coefficient defined by n and m */
  public static Scalar of(int n, int m) {
    if (0 <= n) // 0 <= n
      return CACHE.apply(n).over(m); // n non-negative is guaranteed
    if (0 <= m) { // n < 0 && 0 <= m
      // Binomial[n, k], (-1)^k Binomial[-n + k - 1, k]
      Scalar scalar = CACHE.apply(-n + m - 1).over(m); // (-n + m - 1) non-negative is guaranteed
      return Integers.isEven(m) ? scalar : scalar.negate();
    }
    // n < 0 && m < 0
    // Binomial[n, k] == (-1)^(k + n) Binomial[-k - 1, -n - 1]
    Scalar scalar = CACHE.apply(-m - 1).over(-n - 1);
    return Integers.isEven(m + n) ? scalar : scalar.negate();
  }

  /***************************************************/
  /** @param n non-negative integer
   * @return binomial function that computes n choose k */
  public static Binomial of(Scalar n) {
    return of(Scalars.intValueExact(n));
  }

  /** @param n non-negative
   * @return binomial function that computes n choose k */
  public static Binomial of(int n) {
    return CACHE.apply(Integers.requirePositiveOrZero(n));
  }

  /***************************************************/
  // Binomial[32, 16] == 601080390
  private static final int THRESHOLD = 32;
  private final int n;
  private final Tensor row;

  /** @param n non-negative */
  private Binomial(int n) {
    this.n = n;
    if (n < THRESHOLD) {
      int half = n / 2;
      Scalar x = RealScalar.ONE;
      row = Tensors.reserve(half + 1).append(x);
      for (int k = 1; k <= half; ++k)
        row.append(x = x.multiply(RationalScalar.of(n - k + 1, k)));
    } else
      row = Tensors.of(RealScalar.ONE);
  }

  /** @param k
   * @return n choose k */
  public Scalar over(int k) {
    k = Math.min(k, Math.subtractExact(n, k));
    if (0 <= k) {
      if (k < row.length())
        return row.Get(k);
      synchronized (this) {
        Scalar x = Last.of(row);
        for (int j = row.length(); j <= k; ++j)
          row.append(x = x.multiply(RationalScalar.of(n - j + 1, j)));
        return x;
      }
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%d]", getClass().getSimpleName(), n);
  }
}
