// code by jph
package ch.alpine.tensor.red;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Commonest.html">Commonest</a> */
public enum Commonest {
  ;
  /** @param tensor
   * @return list of elements that have the maximal occurrence in the given tensor */
  public static Tensor of(Tensor tensor) {
    Map<Tensor, Long> map = Tally.of(tensor);
    Optional<Long> optional = map.values().stream().reduce(Math::max);
    if (optional.isPresent()) {
      long value = optional.get();
      return Tensor.of(map.entrySet().stream() //
          .filter(entry -> entry.getValue() == value) //
          .map(Entry::getKey) //
          .map(Tensor::copy));
    }
    return Tensors.empty();
  }
}
