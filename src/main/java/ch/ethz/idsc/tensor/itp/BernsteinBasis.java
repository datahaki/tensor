// code by jph
package ch.ethz.idsc.tensor.itp;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.pdf.BinomialDistribution;
import ch.ethz.idsc.tensor.sca.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BernsteinBasis.html">BernsteinBasis</a>
 * 
 * @see BinomialDistribution */
public enum BernsteinBasis {
  ;
  /** @param n
   * @param p
   * @return vector of length n + 1 */
  public static Tensor of(int n, Scalar p) {
    Integers.requirePositiveOrZero(n);
    boolean revert = Scalars.lessThan(RationalScalar.HALF, p);
    Scalar q = revert //
        ? RealScalar.ONE.subtract(p)
        : p;
    Scalar _1_q = RealScalar.ONE.subtract(q); // 1 - q
    Scalar last = Power.of(_1_q, n);
    Tensor table = Tensors.reserve(n + 1).append(last);
    final Scalar pratio = q.divide(_1_q);
    for (int k = 1; k <= n; ++k) {
      // ((1 - k + n) p) / (k - k p) == ((1 - k + n)/k) * (p/(1 - p))
      Scalar ratio = RationalScalar.of(n - k + 1, k).multiply(pratio);
      last = last.multiply(ratio);
      table.append(last);
    }
    return revert //
        ? Reverse.of(table)
        : table;
  }
}
