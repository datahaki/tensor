// code by jph
package ch.alpine.tensor.itp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.sca.pow.Power;

/** evaluation of Bernstein basis is used in binomial distribution and Bezier curves
 * 
 * <p>the implementation makes use of the relation
 * ((1 - k + n) p) / (k - k p) == ((1 - k + n)/k) * (p/(1 - p))
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
   * @throws Exception if n is negative
   * @throws Exception if p is not an instance of {@link RealScalar} */
  public static Tensor of(int n, Scalar p) {
    Integers.requirePositiveOrZero(n);
    boolean reverse = Scalars.lessThan(RationalScalar.HALF, p);
    if (reverse)
      p = p.one().subtract(p);
    Scalar _1_p = p.one().subtract(p); // 1 - p
    List<Tensor> list = new ArrayList<>(n + 1);
    Scalar last = Power.of(_1_p, n);
    list.add(last);
    Scalar pratio = p.divide(_1_p);
    for (int k = 1; k <= n; ++k)
      list.add(last = last.multiply(RationalScalar.of(n - k + 1, k).multiply(pratio)));
    if (reverse)
      Collections.reverse(list);
    return Unprotect.using(list);
  }
}
