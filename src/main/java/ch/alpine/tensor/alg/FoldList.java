// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.Gatherers;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.chq.ScalarQ;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FoldList.html">FoldList</a> */
public enum FoldList {
  ;
  /** <pre>
   * FoldList[f, {a, b, c, ...}] gives {a, f[a, b], f[f[a, b], c], ...}
   * </pre>
   * 
   * @param binaryOperator
   * @param tensor must not be a {@link Scalar}
   * @return see description above */
  public static Tensor of(TensorBinaryOperator binaryOperator, Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    Tensor head = tensor.get(0);
    return Tensor.of(Stream.concat( //
        Stream.of(head), //
        tensor.stream().skip(1).gather(Gatherers.scan(() -> head, binaryOperator))));
  }

  /** <pre>
   * FoldList[f, x, {a, b, ...}] gives {x, f[x, a], f[f[x, a], b], ...}
   * </pre>
   * 
   * @param binaryOperator
   * @param x
   * @param tensor
   * @return */
  public static Tensor of(TensorBinaryOperator binaryOperator, Tensor x, Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    return Tensor.of(Stream.concat( //
        Stream.of(x.copy()), //
        tensor.stream().gather(Gatherers.scan(() -> x, binaryOperator))));
  }
}
