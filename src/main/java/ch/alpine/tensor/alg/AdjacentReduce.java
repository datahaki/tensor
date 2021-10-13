// code by jph
package ch.alpine.tensor.alg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** operator transforms a given tensor {a, b, c, d, ...} into
 * 
 * <pre>
 * {reduce[a, b], reduce[b, c], reduce[c, d], ...}
 * </pre>
 * 
 * The resulting tensor has the length of the original minus one.
 * 
 * @see Differences */
public abstract class AdjacentReduce implements TensorUnaryOperator {
  /** @param prev
   * @param next
   * @return */
  protected abstract Tensor reduce(Tensor prev, Tensor next);

  @Override
  public final Tensor apply(Tensor tensor) {
    int length = tensor.length();
    if (length <= 1) {
      ScalarQ.thenThrow(tensor);
      return Tensors.empty();
    }
    List<Tensor> list = new ArrayList<>(length - 1);
    Iterator<Tensor> iterator = tensor.iterator();
    for (Tensor prev = iterator.next(); iterator.hasNext();)
      list.add(reduce(prev, prev = iterator.next()));
    return Unprotect.using(list);
  }
}
