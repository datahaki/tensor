// code by jph
package ch.alpine.tensor;

import java.util.List;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;

public enum SparseArrays {
  ;
  /** @param tensor with array structure
   * @param fallback
   * @return
   * @throws Exception if given tensor does not have array structure */
  public static Tensor of(Tensor tensor, Scalar fallback) {
    Dimensions dimensions = new Dimensions(tensor);
    List<Integer> size = dimensions.list();
    if (0 == size.size()) // tensor is a scalar
      return tensor;
    Tensor sparseArray = SparseArray.of(fallback, size.stream().mapToInt(i -> i).toArray());
    Array.forEach(list -> {
      Scalar entry = (Scalar) tensor.get(list); // entry is scalar due to dimension check above
      if (!fallback.equals(entry))
        sparseArray.set(entry, list);
    }, size);
    return sparseArray;
  }
}
