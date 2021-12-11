// code by jph
package ch.alpine.tensor.spa;

import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;

/* package */ enum StaticHelper {
  ;
  /** @param fallback
   * @param size
   * @param tensor
   * @return {@link SparseArray} of given fallback and size equals to tensor, or
   * fallback as {@link Scalar} if tensor equals to fallback */
  public static Tensor of(Scalar fallback, List<Integer> size, Tensor tensor) {
    Tensor sparseArray = SparseArray.of(fallback, size.stream().mapToInt(i -> i).toArray());
    Array.forEach(list -> {
      Scalar entry = (Scalar) tensor.get(list); // entry is scalar due to dimension check above
      if (!fallback.equals(entry))
        sparseArray.set(entry, list);
    }, size);
    return sparseArray;
  }
}
