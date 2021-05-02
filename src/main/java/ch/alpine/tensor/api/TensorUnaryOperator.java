// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Tensor;

/** serializable interface for functions that map a {@link Tensor} to another {@link Tensor} */
@FunctionalInterface
public interface TensorUnaryOperator extends UnaryOperator<Tensor>, Serializable {
  // ---
}
