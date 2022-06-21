// code by jph, gjoel
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Tensor;

/** interface for serializable functions that map a {@link Tensor} to another {@link Tensor} */
@FunctionalInterface
public interface TensorUnaryOperator extends UnaryOperator<Tensor>, Serializable {
  // TODO TENSOR DOC
  default TensorUnaryOperator compose(TensorUnaryOperator before) {
    Objects.requireNonNull(before);
    return s -> apply(before.apply(s));
  }

  default TensorUnaryOperator andThen(TensorUnaryOperator after) {
    Objects.requireNonNull(after);
    return s -> after.apply(apply(s));
  }

  static TensorUnaryOperator identity() {
    return s -> s;
  }
}
