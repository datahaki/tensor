// code by jph
package ch.alpine.tensor;

import java.util.List;
import java.util.NavigableMap;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.Lists;

/** API EXPERIMENTAL
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SparseArray.html">SparseArray</a> */
public enum SparseArrays {
  ;
  /** @param size non-empty
   * @param fallback zero element
   * @param navigableMap
   * @return */
  public static Tensor of(List<Integer> size, Scalar fallback, NavigableMap<Integer, Tensor> navigableMap) {
    Integers.requirePositive(size.size());
    if (!fallback.one().zero().equals(fallback))
      throw TensorRuntimeException.of(fallback);
    if (!navigableMap.isEmpty()) {
      int length = size.get(0);
      requireInRange(navigableMap.firstKey(), length);
      requireInRange(navigableMap.lastKey(), length);
    }
    if (navigableMap.values().stream() //
        .map(Dimensions::of) //
        .allMatch(Lists.rest(size)::equals))
      return new SparseArray(size, fallback, navigableMap);
    throw TensorRuntimeException.of();
  }

  /** @param tensor with array structure
   * @param fallback
   * @return
   * @throws Exception if given tensor does not have array structure */
  public static Tensor of(Tensor tensor, Scalar fallback) {
    Dimensions dimensions = new Dimensions(tensor);
    List<Integer> size = dimensions.list();
    if (0 == size.size()) // tensor is a scalar
      return tensor;
    SparseArray sparseArray = new SparseArray(size, fallback);
    Array.forEach(list -> {
      Scalar entry = (Scalar) tensor.get(list); // entry is scalar due to dimension check above
      if (!fallback.equals(entry))
        sparseArray.set(entry, list);
    }, size);
    return sparseArray;
  }

  /* package */ static void requireInRange(int i, int length) {
    if (i < 0 || length <= i)
      throw new IllegalArgumentException();
  }
}
