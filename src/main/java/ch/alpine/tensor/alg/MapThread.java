// code by jph
package ch.alpine.tensor.alg;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MapThread.html">MapThread</a> */
public enum MapThread {
  ;
  /** MapThread[f, List[{a1, a2, a3}, {b1, b2, b3}, ...], 1]
   * gives {f[a1, b1, ...], f[a2, b2, ...], ...}
   * 
   * Implementation consistent with Mathematica.
   * In the special case that list is empty
   * <pre>
   * MapThread[f, {}, 0] == f[]
   * MapThread[f, {}, 1] == {}
   * </pre>
   * 
   * @param function
   * @param list
   * @param level
   * @return */
  public static Tensor of(Function<List<Tensor>, ? extends Tensor> function, List<Tensor> list, int level) {
    if (Integers.requirePositiveOrZero(level) == 0)
      return function.apply(list);
    if (list.stream().map(Tensor::length).distinct().skip(1).findAny().isPresent())
      throw new Throw(list.toArray(Object[]::new));
    return list.isEmpty() //
        ? Tensors.empty()
        : Tensor.of(IntStream.range(0, list.get(0).length()) //
            .mapToObj(index -> of(function, extract(index, list), level - 1)));
  }

  // helper function
  private static List<Tensor> extract(int index, List<Tensor> list) {
    return list.stream().map(tensor -> tensor.get(index)).collect(Collectors.toList());
  }
}
