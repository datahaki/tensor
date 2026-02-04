// code by jph
package ch.alpine.tensor.alg;

import java.util.Objects;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorBinaryOperator;

/** Fold is identical to
 * <pre>
 * [x, tensor.stream()].reduce(binaryOperator).get();
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Fold.html">Fold</a> */
public enum Fold {
  ;
  /** <pre>
   * Fold[f, x, {a, b, ...}] gives f[... f[f[x, a], b], ...]
   * Fold[f, x, {}] == x
   * </pre>
   * 
   * @param binaryOperator
   * @param x
   * @param tensor {a, b, ...}
   * @return */
  public static Tensor of(TensorBinaryOperator binaryOperator, Tensor x, Tensor tensor) {
    Objects.requireNonNull(binaryOperator);
    if (Tensors.isEmpty(tensor))
      return x.copy();
    return Stream.concat(Stream.of(x), tensor.stream()).reduce(binaryOperator).orElseThrow();
  }
}
