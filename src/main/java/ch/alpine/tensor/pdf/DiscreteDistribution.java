// code by jph
package ch.alpine.tensor.pdf;

import java.math.BigInteger;

import ch.alpine.tensor.Scalar;

/** functionality for a discrete probability distribution
 * 
 * BernoulliDistribution
 * BinomialDistribution
 * DiscreteUniformDistribution
 * EmpiricalDistribution
 * GeometricDistribution
 * HypergeometricDistribution
 * NegativeBinomialDistribution
 * PascalDistribution
 * PoissonBinomialDistribution
 * PoissonDistribution
 * 
 * @see UnivariateDistribution */
public interface DiscreteDistribution extends UnivariateDistribution {
  /** @return lowest value a random variable from this distribution may attain */
  BigInteger lowerBound();

  /** @param n
   * @return P(X == n), i.e. probability of random variable X == n */
  Scalar p_equals(BigInteger n);

  default Scalar p_equals(int n) {
    return p_equals(BigInteger.valueOf(n));
  }
}
