// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.IntStream;

import ch.alpine.tensor.ext.Integers;

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
  private final int[] index;
  private final int[] direction;
  private final int total;
  // ---
  private int count = 0;

  private OuterProductStream(Size size, int[] sigma) {
    dims = size.permute(sigma);
    prod = new int[dims.length];
    index = new int[dims.length];
    for (int c0 = 0; c0 < dims.length; ++c0)
      prod[sigma[c0]] = size.prod(c0);
    total = size.total();
    direction = IntStream.range(0, dims.length) //
        .map(c0 -> dims.length - c0 - 1) //
        .toArray();
  }

  private int seed() {
    return Integers.requireEquals(dot(index), 0);
  }

  private boolean hasNext(int value) {
    return count < total;
  }

  private int next(int value) {
    for (int c0 : direction) {
      ++index[c0];
      index[c0] %= dims[c0];
      if (index[c0] != 0)
        break;
    }
    ++count;
    return dot(index);
  }

  private int dot(int[] index) {
    return IntStream.range(0, prod.length).map(i -> prod[i] * index[i]).sum();
  }

  private IntStream stream() {
    return IntStream.iterate(seed(), this::hasNext, this::next);
  }
}
