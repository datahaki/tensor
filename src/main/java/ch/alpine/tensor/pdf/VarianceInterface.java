// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.Scalar;

/** Any {@link Distribution} for which an analytic expression of the variance
 * exists should implement {@link VarianceInterface}.
 * 
 * <p>The function is used in {@link Expectation} to provide the variance of
 * a given {@link Distribution}. */
@FunctionalInterface
public interface VarianceInterface {
  /** @return variance of distribution */
  Scalar variance();
}
