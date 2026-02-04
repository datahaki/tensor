// code by jph
package ch.alpine.tensor.pdf;

import java.math.BigInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.d.GeometricDistribution;
import ch.alpine.tensor.pdf.d.HypergeometricDistribution;
import ch.alpine.tensor.pdf.d.NegativeBinomialDistribution;
import ch.alpine.tensor.pdf.d.PascalDistribution;
import ch.alpine.tensor.pdf.d.PoissonBinomialDistribution;
import ch.alpine.tensor.pdf.d.PoissonDistribution;

/** functionality for a discrete probability distribution
 * evaluation is over RealScalar
 * probability is non-zero only at integer values
 * 
 * @see BernoulliDistribution
 * @see BinomialDistribution
 * @see DiscreteUniformDistribution
 * @see EmpiricalDistribution
 * @see GeometricDistribution
 * @see HypergeometricDistribution
 * @see NegativeBinomialDistribution
 * @see PascalDistribution
 * @see PoissonBinomialDistribution
 * @see PoissonDistribution
 * 
 * @see UnivariateDistribution */
public interface DiscreteDistribution extends UnivariateDistribution {
  /** @param n
   * @return P(X == n), i.e. probability of random variable X == n */
  Scalar p_equals(BigInteger n);
}
