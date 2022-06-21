// code by jph, gjoel
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Tensor;

/** interface for serializable functions that map a {@link Tensor} to another {@link Tensor} */
@FunctionalInterface
public interface TensorUnaryOperator extends UnaryOperator<Tensor>, Serializable {
  TensorUnaryOperator IDENTITY = t -> t;

  /** @param before non-null
   * @return tensor -> apply(before.apply(tensor))
   * @throws Exception if operator before is null */
  default TensorUnaryOperator compose(TensorUnaryOperator before) {
    Objects.requireNonNull(before);
    return tensor -> apply(before.apply(tensor));
  }

  /** @param after non-null
   * @return tensor -> after.apply(apply(tensor))
   * @throws Exception if operator after is null */
  default TensorUnaryOperator andThen(TensorUnaryOperator after) {
    Objects.requireNonNull(after);
    return tensor -> after.apply(apply(tensor));
  }
  // /** slash corresponds to Mathematica's
  // * <pre>
  // * /@
  // * </pre>
  // *
  // * @param tensor
  // * @return {apply(tensor.get(0)), apply(tensor.get(1)), ..., apply(tensor.get(length-1)))} */
  // default Tensor slash(Tensor tensor) {
  // return Tensor.of(tensor.stream().map(this));
  // }

  /** Remark:
   * The empty chain TensorUnaryOperator.chain() returns
   * the instance {@link #IDENTITY}
   * 
   * @param tensorUnaryOperators
   * @return operator that nests given operators with execution from left to right
   * @throws Exception if any given operator is null */
  @SafeVarargs
  static TensorUnaryOperator chain(TensorUnaryOperator... tensorUnaryOperators) {
    return List.of(tensorUnaryOperators).stream() //
        .reduce(IDENTITY, TensorUnaryOperator::andThen);
  }
}
