// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.IntStream;

/** utility class for {@link Transpose} */
/* package */ class OuterProductStream {
  /** @param size
   * @param sigma
   * @param forward
   * @return */
  public static IntStream of(Size size, int[] sigma, boolean forward) {
    return new OuterProductStream(size, sigma, forward).stream();
  }

  // ---
  private final int[] size;
  private final Size _size;
  private final int[] sigma;
  private final int[] index;
  private final int[] direction;
  private final int total;
  // ---
  private int count = 0;

  private OuterProductStream(Size _size, int[] sigma, boolean forward) {
    this.size = _size.permute(sigma).size();
    this._size = _size;
    this.sigma = sigma;
    int total = 1;
    index = new int[size.length];
    for (int c0 = 0; c0 < size.length; ++c0) {
      index[c0] = 0;
      total *= size[c0];
    }
    this.total = total;
    direction = IntStream.range(0, size.length) //
        .map(c0 -> forward ? size.length - c0 - 1 : c0) //
        .toArray();
  }

  private int seed() {
    return _size.indexOf(index, sigma);
  }

  private boolean hasNext(int list) {
    return count < total;
  }

  private int next(int list) {
    for (int c0 : direction) {
      ++index[c0];
      index[c0] %= size[c0];
      if (index[c0] != 0)
        break;
    }
    ++count;
    return _size.indexOf(index, sigma);
  }

  private IntStream stream() {
    return IntStream.iterate(seed(), this::hasNext, this::next);
  }
}
