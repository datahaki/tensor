// code by jph
package ch.alpine.tensor.alg;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensors;

/** utility class for {@link Transpose} */
/* package */ class Size {
  /** @param list for instance Dimensions[tensor]
   * @return */
  public static Size of(List<Integer> list) {
    return new Size(list.stream().mapToInt(i -> i).toArray());
  }

  // ---
  private final int[] size;
  private final int[] prod;

  /** @param size
   * @throws Exception if size.length == 0 */
  private Size(int[] size) {
    this.size = size;
    prod = new int[size.length];
    final int dmo = size.length - 1;
    prod[dmo] = 1;
    for (int index = 0; index < dmo; ++index)
      prod[dmo - (index + 1)] = prod[dmo - index] * size[dmo - index];
  }

  /** Example:
   * { 2, 3, 4 }.Permute[{ 2, 0, 1 }] == {3, 4, 2}
   * 
   * @param sigma
   * @return */
  public Size permute(int[] sigma) {
    int[] dims = new int[sigma.length];
    for (int index = 0; index < sigma.length; ++index)
      dims[sigma[index]] = size[index];
    return new Size(dims);
  }

  public int indexOf(List<Integer> list, int[] sigma) {
    return IntStream.range(0, prod.length) //
        .map(index -> prod[index] * list.get(sigma[index])) //
        .sum();
  }

  public int[] size() {
    return size;
  }

  public Stream<List<Integer>> stream() {
    return OuterProductStream.of(size, true);
  }

  @Override // from Object
  public String toString() {
    return Tensors.vectorInt(size) + ".." + Tensors.vectorInt(prod);
  }
}
