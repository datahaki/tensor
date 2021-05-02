// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
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
  // ---
}
