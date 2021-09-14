// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensors;

/** utility class for {@link Transpose} */
/* package */ class Size implements Iterable<MultiIndex> {
  /** @param dims
   * @throws Exception if dims.length == 0 */
  public static Size of(int[] dims) {
    return new Size(Arrays.copyOf(dims, dims.length));
  }

  /***************************************************/
  private final int[] size;
  private final int[] prod;

  /** @param dims
   * @throws Exception if dims.length == 0 */
  private Size(int[] dims) {
    size = dims;
    prod = new int[dims.length];
    final int dmo = dims.length - 1;
    prod[dmo] = 1;
    for (int index = 0; index < dmo; ++index)
      prod[dmo - (index + 1)] = prod[dmo - index] * size[dmo - index];
  }

  public Size permute(int[] sigma) {
    return new Size(StaticHelper.inverse(size, sigma));
  }

  public int indexOf(int[] _size, int[] sigma) {
    return IntStream.range(0, prod.length) //
        .map(index -> prod[index] * _size[sigma[index]]) //
        .sum();
  }

  public int size(int index) {
    return size[index];
  }

  @Override // from Iterable
  public Iterator<MultiIndex> iterator() {
    return new Iterator<>() {
      final OuterProductInteger outerProductInteger = new OuterProductInteger(size, true);

      @Override
      public boolean hasNext() {
        return outerProductInteger.hasNext();
      }

      @Override
      public MultiIndex next() {
        return new MultiIndex(outerProductInteger.next());
      }
    };
  }

  @Override // from Object
  public String toString() {
    return Tensors.vectorInt(size) + ".." + Tensors.vectorInt(prod);
  }
}
