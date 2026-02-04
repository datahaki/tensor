// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.Gatherers;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ScalarQ;

/** operator transforms a given tensor {a, b, c, d, ..., y, z} into
 * 
 * <pre>
 * {reduce[a, b], reduce[b, c], reduce[c, d], ..., reduce[y, z]}
 * </pre>
 * 
 * The resulting tensor has the length of the original minus one.
 * For empty input the result is the empty tensor.
 * 
 * @see Differences */
public record AdjacentReduce(TensorBinaryOperator reduce) implements TensorUnaryOperator {
  @Override
  public Tensor apply(Tensor tensor) {
    if (tensor.length() <= 1) {
      ScalarQ.thenThrow(tensor);
      return Tensors.empty();
    }
    return Tensor.of(tensor.stream() //
        .gather(Gatherers.windowSliding(2)) //
        .map(list -> reduce.apply(list.get(0), list.get(1))));
  }
}
