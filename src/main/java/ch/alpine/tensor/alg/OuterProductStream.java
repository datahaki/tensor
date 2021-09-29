// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.IntStream;

/** utility class for {@link Transpose} */
/* package */ class OuterProductStream {
  /** @param size
   * @param sigma
   * @return */
  public static IntStream of(Size size, int[] sigma) {
    return new OuterProductStream(size, sigma).stream();
  }

  // ---
  private final int[] dims;
  private final int[] prod;
  private final int[] cump;
  private final int total;
  private final int[] multi;
  // ---
  private int count = 0;
  private int index = 0;

  private OuterProductStream(Size size, int[] sigma) {
    dims = size.permute(sigma);
    prod = new int[dims.length];
    for (int c0 = 0; c0 < dims.length; ++c0)
      prod[sigma[c0]] = size.prod(c0);
    cump = IntStream.range(0, dims.length) //
        .map(c0 -> prod[c0] * dims[c0]).toArray();
    total = size.total();
    multi = new int[dims.length];
  }

  private int seed() {
    return index;
  }

  private boolean hasNext(int value) {
    return count < total;
  }

  private int next(int value) {
    for (int c0 = dims.length - 1; 0 <= c0; --c0) {
      ++multi[c0];
      multi[c0] %= dims[c0];
      index += prod[c0];
      if (multi[c0] != 0)
        break;
      index -= cump[c0];
    }
    ++count;
    return index;
  }

  private IntStream stream() {
    return IntStream.iterate(seed(), this::hasNext, this::next);
  }
}
