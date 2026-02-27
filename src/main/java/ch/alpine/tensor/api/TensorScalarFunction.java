// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** serializable function that maps a {@link Tensor} to a {@link Scalar}
 * 
 * <p>Examples:
 * 1) an implicit function that defines a region as {x | f(x) &lt; 0}
 * 2) a smooth noise function that maps a vector to a value in the interval [-1, 1] */
@FunctionalInterface
public interface TensorScalarFunction extends Function<Tensor, Scalar>, Serializable {
  /** @param before non-null
   * @return scalar -> apply(before.apply(scalar))
   * @throws Exception if operator before is null */
  default TensorScalarFunction compose(TensorUnaryOperator before) {
    Objects.requireNonNull(before);
    return tensor -> apply(before.apply(tensor));
  }

  /** @param after non-null
   * @return tensor -> after.apply(apply(tensor))
   * @throws Exception if operator after is null */
  default TensorScalarFunction andThen(ScalarUnaryOperator after) {
    Objects.requireNonNull(after);
    return tensor -> after.apply(apply(tensor));
  }

  default Tensor slash(Tensor tensor) {
    return Tensor.of(tensor.stream().map(this));
  }
}
