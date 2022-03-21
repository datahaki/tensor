// code by jph
package ch.alpine.tensor.spa;

import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Normal.html">Normal</a> */
public class Normal implements TensorUnaryOperator {
  private static final TensorUnaryOperator IDENTITY = new Normal(s -> s);

  /** Converts {@link SparseArray} to full tensor
   * 
   * <p>Special case:
   * Mathematica::Normal[3] == 3
   * 
   * @param tensor
   * @return */
  public static Tensor of(Tensor tensor) {
    return IDENTITY.apply(tensor);
  }

  private final Function<Scalar, ? extends Tensor> function;

  /* package */ Normal(Function<Scalar, ? extends Tensor> function) {
    this.function = function;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return tensor instanceof Scalar //
        ? function.apply((Scalar) tensor)
        : Tensor.of(tensor.stream().map(this::apply));
  }
}
