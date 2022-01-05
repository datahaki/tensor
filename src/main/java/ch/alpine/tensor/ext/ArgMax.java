// code by jph
package ch.alpine.tensor.ext;

import java.util.Comparator;
import java.util.Objects;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArgMax.html">ArgMax</a> */
public class ArgMax<T> extends ArgBase<T> {
  /** @param iterable for instance instance of list
   * @param comparator
   * @return index of maximum entry in iterable according to comparator,
   * or -1 if iterable is empty */
  public static <T> int of(Iterable<T> iterable, Comparator<T> comparator) {
    return new ArgMax<T>().find(iterable, Objects.requireNonNull(comparator));
  }

  /** Examples:
   * <pre>
   * ArgMax.of({3, 4, 2, 0, 3}) == 1
   * ArgMax.of({4, 3, 2, 4, 3}) == 0
   * </pre>
   * 
   * @param iterable for instance instance of list
   * @return index of maximum entry in iterable, or -1 if iterable is empty */
  public static <T> int of(Iterable<T> iterable) {
    return new ArgMax<T>().find(iterable);
  }

  private ArgMax() {
    // ---
  }

  @Override // from IntPredicate
  public boolean test(int value) {
    return value < 0;
  }
}
