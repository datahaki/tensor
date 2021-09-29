// code by jph
package ch.alpine.tensor.alg;

import java.util.List;
import java.util.stream.IntStream;

import ch.alpine.tensor.ext.Integers;

/** utility class for {@link Transpose} */
/* package */ class Size {
  /** @param list for instance Dimensions[tensor]
   * @return */
  public static Size of(List<Integer> list) {
    return new Size(list.stream().mapToInt(Integer::intValue).toArray());
  }

  // ---
  private final int[] size;
  private final int[] prod;

  /** @param size
   * @throws Exception if size.length == 0 */
  private Size(int[] size) {
    this.size = size;
    prod = new int[size.length];
    int last = size.length - 1;
    prod[last] = 1;
    for (int index = last; 0 < index; --index)
      prod[index - 1] = Math.multiplyExact(prod[index], size[index]);
  }

  public int total() {
    return Math.multiplyExact(prod[0], size[0]);
  }

  /** Example:
   * { 2, 3, 4 }.Permute[{ 2, 0, 1 }] == {3, 4, 2}
   * 
   * @param sigma
   * @return */
  public int[] permute(int[] sigma) {
    Integers.requirePermutation(sigma);
    Integers.requireEquals(size.length, sigma.length);
    int[] dims = new int[sigma.length];
    for (int index = 0; index < sigma.length; ++index)
      dims[sigma[index]] = size[index];
    return dims;
  }

  public int prod(int c0) {
    return prod[c0];
  }

  public IntStream stream(int[] sigma) {
    return OuterProductStream.of(this, sigma);
  }
}
