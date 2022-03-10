// code by jph
package ch.alpine.tensor.alg;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ListConvolve.html">ListConvolve</a> */
public enum Riffle {
  ;
  /** @param tensor
   * @param element
   * @return */
  public static Tensor of(Tensor tensor, Tensor element) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    List<Tensor> list = new ArrayList<>(tensor.length() * 2 - 1);
    list.add(tensor.get(0));
    for (int index = 1; index < tensor.length(); ++index) {
      list.add(element);
      list.add(tensor.get(index));
    }
    return Unprotect.using(list);
  }
}
