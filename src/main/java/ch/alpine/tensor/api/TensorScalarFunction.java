// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** serializable function that maps a {@link Tensor} to a {@link Scalar}
 * 
 * Examples:
 * 1) an implicit function that defines a region as {x | f(x) < 0}
 * 2) a smooth noise function that maps a vector to a value in the interval [-1, 1] */
@FunctionalInterface
public interface TensorScalarFunction extends Function<Tensor, Scalar>, Serializable {
  // ---
}
