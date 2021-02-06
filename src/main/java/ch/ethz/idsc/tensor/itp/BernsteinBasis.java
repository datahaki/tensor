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

/** table is used in binomial distribution and bezier curves
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BernsteinBasis.html">BernsteinBasis</a>
 * 
 * @see BinomialDistribution */
public enum BernsteinBasis {
  ;
  /** @param n degree of polynomials, non-negative
   * @param p typically in the unit interval [0, 1] but the evaluation is not
   * restricted to any range
   * @return vector of length n + 1
   * @throws Exception if n is negative */
  public static Tensor of(int n, Scalar p) {
    Integers.requirePositiveOrZero(n);
    boolean reverse = Scalars.lessThan(RationalScalar.HALF, p);
    if (reverse)
      p = RealScalar.ONE.subtract(p);
    Scalar _1_p = RealScalar.ONE.subtract(p); // 1 - p
    Scalar last = Power.of(_1_p, n);
    Tensor table = Tensors.reserve(n + 1).append(last);
    Scalar pratio = p.divide(_1_p);
    for (int k = 1; k <= n; ++k)
      // ((1 - k + n) p) / (k - k p) == ((1 - k + n)/k) * (p/(1 - p))
      table.append(last = last.multiply(RationalScalar.of(n - k + 1, k).multiply(pratio)));
    return reverse //
        ? Reverse.of(table)
        : table;
  }
}
