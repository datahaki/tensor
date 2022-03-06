// code by jph
package ch.alpine.tensor.alg;

import java.util.function.BiFunction;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Outer.html">Outer</a> */
public enum Outer {
  ;
  /** @param binaryOperator that receives copies of entries from a and b
   * @param a
   * @param b
   * @return tensor of dimensions [a.length(), b.length(), ...] */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor, U extends Tensor> //
  Tensor of(BiFunction<T, U, ? extends Tensor> binaryOperator, Tensor a, Tensor b) {
    // return Tensor.of(a.stream() //
    // .map(x_row -> Tensor.of(b.stream() //
    // .map(y_row -> binaryOperator.apply((T) x_row, (U) y_row)))));
    return Tensors.matrix((i, j) -> binaryOperator.apply((T) a.get(i), (U) b.get(j)), a.length(), b.length());
  }
}
