// code by jph
package ch.alpine.tensor.num;

import java.io.Serializable;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Sign;

/** binomial coefficient implemented for integer input
 * <pre>
 * Gamma[n+1] / ( Gamma[m+1] Gamma[n-m+1] )
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Binomial.html">Binomial</a> */
/* package */ class BinomialExt implements Serializable {
  private static final int MAX_SIZE = 384;
  private static final Function<Scalar, BinomialExt> CACHE = Cache.of(BinomialExt::new, MAX_SIZE);

  // ---
  /** @param n non-negative integer
   * @return binomial function that computes n choose k */
  public static BinomialExt of(Scalar n) {
    return CACHE.apply(n);
  }

  // ---
  private final Scalar n;
  private final NavigableMap<Scalar, Scalar> row = new TreeMap<>();

  /** @param n non-negative */
  private BinomialExt(Scalar n) {
    this.n = n;
    row.put(n.zero(), n.one());
  }

  /** @param k
   * @return n choose k */
  public Scalar over(Scalar k) {
    k = Min.of(k, n.subtract(k));
    if (Sign.isPositiveOrZero(k)) {
      // QUEST TENSOR NUM implement to work for GaussScalar
      // if (k < row.length())
      // return row.Get(k);
      // synchronized (this) {
      // Scalar x = Last.of(row);
      // for (int j = row.length(); j <= k; ++j)
      // row.append(x = x.multiply(RationalScalar.of(n - j + 1, j)));
      // return x;
      // }
    }
    return k.zero();
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("BinomialExt", n);
  }
}
