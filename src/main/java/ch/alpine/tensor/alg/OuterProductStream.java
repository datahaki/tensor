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
  /** cummulative product */
  private final int[] cump;
  /** multi-index */
  private final int[] mind;
  private final int total;
  // ---
  private int count = 0;

  private OuterProductStream(Size size, int[] sigma) {
    dims = size.permute(sigma);
    prod = new int[dims.length];
    for (int c0 = 0; c0 < dims.length; ++c0)
      prod[sigma[c0]] = size.prod(c0);
    cump = IntStream.range(0, dims.length) //
        .map(c0 -> prod[c0] * dims[c0]).toArray();
    mind = new int[dims.length];
    total = size.total();
  }

  private boolean hasNext(int index) {
    return count < total;
  }

  private int next(int index) {
    for (int c0 = dims.length - 1; 0 <= c0; --c0) {
      ++mind[c0];
      mind[c0] %= dims[c0];
      index += prod[c0];
      if (mind[c0] != 0)
        break;
      index -= cump[c0];
    }
    ++count;
    return index;
  }

  private IntStream stream() {
    return IntStream.iterate(0, this::hasNext, this::next);
  }
}
