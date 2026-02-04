// code by jph
package ch.alpine.tensor.red;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Commonest.html">Commonest</a> */
public enum Commonest {
  ;
  /** @param tensor
   * @return list of elements that have the maximal occurrence in the given tensor */
  public static Tensor of(Tensor tensor) {
    return of(tensor.stream());
  }

  /** @param stream
   * @return list of elements that have the maximal occurrence in the given stream of tensors */
  public static Tensor of(Stream<Tensor> stream) {
    Map<Tensor, Long> map = Tally.of(stream);
    Optional<Long> optional = map.values().stream().reduce(Math::max);
    if (optional.isPresent()) {
      long value = optional.orElseThrow();
      return Tensor.of(map.entrySet().stream() //
          .filter(entry -> entry.getValue() == value) //
          .map(Entry::getKey) //
          .map(Tensor::copy));
    }
    return Tensors.empty();
  }
}
