// code by jph
package ch.alpine.tensor.ext;

import java.util.Comparator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArgMin.html">ArgMin</a> */
public class ArgMin extends ArgBase {
  /** @param iterable
   * @param comparator
   * @return index of minimum entry in iterable according to comparator,
   * or -1 if iterable is empty */
  public static <T> int of(Iterable<T> iterable, Comparator<T> comparator) {
    return new ArgMin().find(iterable, comparator);
  }

  /** Examples:
   * <pre>
   * ArgMin.of({3, 4, 1, 2, 3}) == 2
   * ArgMin.of({1, 4, 1, 2, 3}) == 0
   * </pre>
   * 
   * @param iterable
   * @return index of minimum entry in iterable, or -1 if iterable is empty */
  public static <T> int of(Iterable<T> iterable) {
    return new ArgMin().find(iterable);
  }

  private ArgMin() {
    // ---
  }

  @Override // from IntPredicate
  public boolean test(int value) {
    return 0 < value;
  }
}
