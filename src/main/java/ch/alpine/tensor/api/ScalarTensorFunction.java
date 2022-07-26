// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.itp.BSplineFunction;

/** serializable function that maps a {@link Scalar} to a {@link Tensor}
 * 
 * Examples: {@link ColorDataGradient}, and {@link BSplineFunction} */
@FunctionalInterface
public interface ScalarTensorFunction extends Function<Scalar, Tensor>, Serializable {
  /** @param before non-null
   * @return scalar -> apply(before.apply(scalar))
   * @throws Exception if operator before is null */
  default ScalarTensorFunction compose(ScalarUnaryOperator before) {
    Objects.requireNonNull(before);
    return tensor -> apply(before.apply(tensor));
  }

  /** @param after non-null
   * @return scalar -> after.apply(apply(scalar))
   * @throws Exception if operator after is null */
  default ScalarTensorFunction andThen(TensorUnaryOperator after) {
    Objects.requireNonNull(after);
    return scalar -> after.apply(apply(scalar));
  }
}
