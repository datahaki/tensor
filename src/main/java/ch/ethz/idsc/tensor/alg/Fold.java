// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.Objects;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;

/** inspired by
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
  public static Tensor of(BinaryOperator<Tensor> binaryOperator, Tensor x, Tensor tensor) {
    int length = tensor.length();
    if (length == 0) {
      Objects.requireNonNull(binaryOperator);
      return x.copy();
    }
    ScalarQ.thenThrow(tensor);
    for (int index = 0; index < length; ++index)
      x = binaryOperator.apply(x, tensor.get(index));
    return x;
  }
}
