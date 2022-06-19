// code by jph
package ch.alpine.tensor.pdf;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.d.GeometricDistribution;

/** cumulative distribution function
 * 
 * CDF extends the capabilities of {@link PDF}
 * 
 * {@link DiscreteDistribution}s may extend CDF if the implementation
 * is beneficial for computational efficiency and numerical robustness.
 * Examples: {@link DiscreteUniformDistribution}, and {@link GeometricDistribution}.
 * 
 * Remark:
 * For discrete distributions
 * Mathematica::CDF[distribution, x] == distribution.p_lessEquals(x)
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CDF.html">CDF</a> */
public interface CDF {
  /** @param distribution
   * @return cumulative distribution function
   * @throws Exception if distribution does not support the computation of the CDF */
  static CDF of(Distribution distribution) {
    if (distribution instanceof CDF)
      return (CDF) distribution;
    Objects.requireNonNull(distribution);
    throw new IllegalArgumentException(distribution.getClass().getName());
  }

  /** @param x
   * @return P(X < x), i.e. probability of random variable X < x */
  Scalar p_lessThan(Scalar x);

  /** For discrete distributions {@link #p_lessEquals(Scalar)} corresponds to
   * Mathematica::CDF[distribution, x]
   * 
   * @param x
   * @return P(X <= x), i.e. probability of random variable X <= x */
  Scalar p_lessEquals(Scalar x);
}
