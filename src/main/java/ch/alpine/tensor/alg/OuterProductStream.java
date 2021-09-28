// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** utility class for {@link Transpose} */
/* package */ class OuterProductStream {
  /** @param size
   * @param forward */
  public static Stream<List<Integer>> of(int[] size, boolean forward) {
    return new OuterProductStream(size, forward).stream();
  }

  // ---
  private final Integer[] index;
  private final int[] size;
  private final int[] direction;
  private final int total;
  // ---
  private int count = 0;

  private OuterProductStream(int[] size, boolean forward) {
    this.size = size;
    int total = 1;
    index = new Integer[size.length];
    for (int c0 = 0; c0 < size.length; ++c0) {
      index[c0] = 0;
      total *= size[c0];
    }
    this.total = total;
    direction = IntStream.range(0, size.length) //
        .map(c0 -> forward ? size.length - c0 - 1 : c0) //
        .toArray();
  }

  private List<Integer> seed() {
    return Arrays.asList(index);
  }

  private boolean hasNext(List<Integer> list) {
    return count < total;
  }

  private List<Integer> next(List<Integer> list) {
    for (int c0 : direction) {
      ++index[c0];
      index[c0] %= size[c0];
      if (index[c0] != 0)
        break;
    }
    ++count;
    // careful: one mutable reference is used for all elements in stream
    return Arrays.asList(index);
  }

  private Stream<List<Integer>> stream() {
    return Stream.iterate(seed(), this::hasNext, this::next);
  }
}
