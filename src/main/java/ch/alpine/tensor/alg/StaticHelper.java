// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.Primitives;

/* package */ enum StaticHelper {
  ;
  /** @param list
   * @param sigma
   * @return */
  public static <T> List<T> reorder(List<T> list, int[] sigma) {
    return Arrays.stream(sigma).mapToObj(list::get).collect(Collectors.toList());
  }

  /** same as function above but implemented for different input and output type
   * 
   * @param size
   * @param sigma
   * @return */
  public static <T> List<T> inverse(List<T> size, Tensor sigma) {
    return reorder(size, inverse(Primitives.toIntArray(sigma))); //
  }

  /** Hint: function is a special case of permute with size==Range
   * 
   * <pre>
   * inverse(inverse(x)) == x
   * </pre>
   * 
   * @param sigma
   * @return inverse({0,1,2,...}, sigma) */
  @PackageTestAccess
  static int[] inverse(int[] sigma) {
    int[] dims = new int[sigma.length];
    for (int index = 0; index < sigma.length; ++index)
      dims[sigma[index]] = index;
    return dims;
  }
}
