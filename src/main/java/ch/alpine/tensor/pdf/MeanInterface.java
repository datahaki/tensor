// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;

/** Any {@link Distribution} for which an analytic expression of the mean
 * exists should implement {@link MeanInterface}.
 * 
 * <p>The function is used in {@link Expectation} to provide the mean of
 * a given {@link Distribution}. */
@FunctionalInterface
public interface MeanInterface {
  /** Example:
   * <pre>
   * Expectation.mean(BinomialDistribution.of(n, p)) == n * p
   * </pre>
   * 
   * @return mean of distribution */
  Scalar mean();
}
