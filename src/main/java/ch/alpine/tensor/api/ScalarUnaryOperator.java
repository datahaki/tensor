// code by jph, gjoel
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Scalar;

/** interface for serializable functions that map a {@link Scalar} to another {@link Scalar} */
@FunctionalInterface
public interface ScalarUnaryOperator extends UnaryOperator<Scalar>, Serializable {
  /** @param before non-null
   * @return scalar -> apply(before.apply(scalar))
   * @throws Exception if operator before is null */
  default ScalarUnaryOperator compose(ScalarUnaryOperator before) {
    Objects.requireNonNull(before);
    return scalar -> apply(before.apply(scalar));
  }

  /** @param after non-null
   * @return scalar -> after.apply(apply(scalar))
   * @throws Exception if operator after is null */
  default ScalarUnaryOperator andThen(ScalarUnaryOperator after) {
    Objects.requireNonNull(after);
    return scalar -> after.apply(apply(scalar));
  }
}
