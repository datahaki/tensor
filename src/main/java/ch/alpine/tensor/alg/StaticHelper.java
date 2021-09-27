// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.Primitives;

/* package */ enum StaticHelper {
  ;
  /** Example:
   * Permute[{ 2, 3, 4 }, { 2, 0, 1 }] == {3, 4, 2}
   * 
   * @param size
   * @param sigma
   * @return */
  public static int[] inverse(int[] size, int[] sigma) {
    int[] dims = new int[sigma.length];
    for (int index = 0; index < sigma.length; ++index)
      dims[sigma[index]] = size[index];
    return dims;
  }

  /** same as function above but implemented for different input and output type
   * 
   * @param size
   * @param sigma
   * @return */
  public static List<Integer> inverse(List<Integer> size, Tensor sigma) {
    return reorder(size, inverse(Primitives.toIntArray(sigma))); //
  }

  /** Hint: function is a special case of permute with size==Range
   * 
   * <pre>
   * inverse(inverse(x)) == x
   * </pre>
   * 
   * function only used within static helper
   * 
   * @param sigma
   * @return inverse({0,1,2,...}, sigma) */
  public static int[] inverse(int[] sigma) {
    int[] dims = new int[sigma.length];
    for (int index = 0; index < sigma.length; ++index)
      dims[sigma[index]] = index;
    return dims;
  }

  /** Hint: not the same as {@link #inverse(int[], int[])}
   * 
   * @param list
   * @param sigma
   * @return */
  public static List<Integer> reorder(List<Integer> list, int[] sigma) {
    return Arrays.stream(sigma).mapToObj(list::get).collect(Collectors.toList());
  }
}
