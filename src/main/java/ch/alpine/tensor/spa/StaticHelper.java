// code by jph
package ch.alpine.tensor.spa;

import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Array;

/* package */ enum StaticHelper {
  ;
  /** @param fallback
   * @param size
   * @param tensor
   * @return instance of {@link SparseArray} equals to tensor */
  public static Tensor of(Scalar fallback, List<Integer> size, Tensor tensor) {
    Tensor sparseArray = SparseArray.of(fallback, size.stream().mapToInt(i -> i).toArray());
    Array.forEach(list -> {
      Scalar entry = (Scalar) tensor.get(list); // entry is scalar due to dimension check above
      if (!fallback.equals(entry))
        sparseArray.set(entry, list);
    }, size);
    return sparseArray;
  }

  /** @param fallback
   * @return */
  public static Scalar checkFallback(Scalar fallback) {
    if (fallback.one().zero().equals(fallback))
      return fallback;
    throw TensorRuntimeException.of(fallback);
  }
}
