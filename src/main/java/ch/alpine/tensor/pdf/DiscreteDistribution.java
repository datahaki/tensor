// code by jph
package ch.alpine.tensor.pdf;

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
  int lowerBound();

  /** @param n
   * @return P(X == n), i.e. probability of random variable X == n */
  Scalar p_equals(int n);
}
