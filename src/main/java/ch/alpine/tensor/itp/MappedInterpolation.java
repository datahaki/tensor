// code by jph
package ch.alpine.tensor.itp;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ImageResize;

/** interpolation maps a given tensor to an integer index via a user specified function.
 * 
 * @see ImageResize */
public class MappedInterpolation extends AbstractInterpolation implements Serializable {
  /** @param tensor non-null
   * @param function non-null
   * @return
   * @throws Exception if given tensor is null */
  public static Interpolation of(Tensor tensor, ScalarUnaryOperator function) {
    return new MappedInterpolation(tensor, Objects.requireNonNull(function));
  }

  // ---
  private final Tensor tensor;
  private final ScalarUnaryOperator function;

  private MappedInterpolation(Tensor tensor, ScalarUnaryOperator function) {
    this.tensor = Objects.requireNonNull(tensor);
    this.function = function;
  }

  @Override // from Interpolation
  public final Tensor get(Tensor index) {
    return tensor.get(index.stream() //
        .map(Scalar.class::cast) //
        .map(function) //
        .map(Scalars::intValueExact) //
        .toArray(Integer[]::new));
  }

  @Override // from Interpolation
  public final Tensor at(Scalar index) {
    return tensor.get(Scalars.intValueExact(function.apply(index)));
  }
}
