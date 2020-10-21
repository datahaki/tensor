// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

/** result of optimization is typically
 * 1) approximate, and
 * 2) available only in numerical precision
 * 3) non-optimal for rare special inputs
 * 
 * https://en.wikipedia.org/wiki/Fermat%E2%80%93Weber_problem
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpatialMedian.html">SpatialMedian</a> */
public interface SpatialMedian {
  /** @param sequence of points
   * @return point minimizing the sum of distances from given points, or empty,
   * if no such point could be computed with the given tolerance */
  Optional<Tensor> uniform(Tensor sequence);

  /** "Weber problem"
   * 
   * @param sequence of points
   * @param weights vector with entries that sum up to 1
   * @return point minimizing the sum of weighted distances from given (point, weight) pairs,
   * or empty, if no such point could be computed with the given tolerance */
  Optional<Tensor> weighted(Tensor sequence, Tensor weights);
}
