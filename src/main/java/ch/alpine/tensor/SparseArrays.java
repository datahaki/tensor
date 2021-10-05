// code by jph
package ch.alpine.tensor;

import java.util.List;
import java.util.NavigableMap;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;

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
    if (Scalars.nonZero(fallback))
      throw TensorRuntimeException.of(fallback);
    if (!navigableMap.isEmpty()) {
      requireInRange(navigableMap.firstKey(), size.get(0));
      requireInRange(navigableMap.lastKey(), size.get(0));
    }
    return new SparseArray(size, fallback, navigableMap);
  }

  /** @param tensor
   * @param fallback
   * @return
   * @throws Exception if given tensor does not have array structure */
  public static Tensor of(Tensor tensor, Scalar fallback) {
    Dimensions dimensions = new Dimensions(tensor);
    if (dimensions.isArray()) {
      List<Integer> size = dimensions.list();
      if (0 == size.size())
        return tensor;
      SparseArray sparseArray = new SparseArray(size, fallback);
      Array.forEach(list -> {
        Tensor entry = tensor.get(list); // entry is scalar due to dimension check above
        if (!fallback.equals(entry))
          sparseArray.set(entry, list);
      }, size);
      return sparseArray;
    }
    throw TensorRuntimeException.of(tensor);
  }

  /* package */ static void requireInRange(int i, int length) {
    if (i < 0 || length <= i)
      throw new IllegalArgumentException();
  }
}
