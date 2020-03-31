// code by jph
package ch.ethz.idsc.tensor.opt;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;

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
  /** @param chop non null
   * @return */
  static SpatialMedian with(Chop chop) {
    return new WeiszfeldMethod(Objects.requireNonNull(chop));
  }

  /** @param points
   * @return point minimizing the sum of distances from given points, or empty,
   * if no such point could be computed with default precision */
  static Optional<Tensor> of(Tensor points) {
    return WeiszfeldMethod.DEFAULT.uniform(points);
  }

  /** @param points
   * @return point minimizing the sum of distances from given points, or empty,
   * if no such point could be computed with the given tolerance */
  Optional<Tensor> uniform(Tensor points);

  /** "Weber problem"
   * 
   * @param points
   * @param weights
   * @return point minimizing the sum of weighted distances from given (point, weight) pairs,
   * or empty, if no such point could be computed with the given tolerance */
  Optional<Tensor> weighted(Tensor points, Tensor weights);
}
