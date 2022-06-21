// code by jph, gjoel
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Scalar;

/** interface for serializable functions that map a {@link Scalar} to another {@link Scalar} */
@FunctionalInterface
public interface ScalarUnaryOperator extends UnaryOperator<Scalar>, Serializable {
  // TODO TENSOR DOC
  default ScalarUnaryOperator compose(ScalarUnaryOperator before) {
    Objects.requireNonNull(before);
    return s -> apply(before.apply(s));
  }

  default ScalarUnaryOperator andThen(ScalarUnaryOperator after) {
    Objects.requireNonNull(after);
    return s -> after.apply(apply(s));
  }

  static ScalarUnaryOperator identity() {
    return s -> s;
  }
}
