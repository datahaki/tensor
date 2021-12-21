// code by jph
package ch.alpine.tensor.ext;

import java.util.Comparator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArgMax.html">ArgMax</a> */
public class ArgMax extends ArgBase {
  /** @param iterable
   * @param comparator
   * @return index of maximum entry in tensor according to comparator,
   * or -1 if tensor is empty
   * @throws Exception if given tensor is a scalar */
  public static <T> int of(Iterable<T> iterable, Comparator<T> comparator) {
    return new ArgMax().find(iterable, comparator);
  }

  /** Examples:
   * <pre>
   * ArgMax.of({3, 4, 2, 0, 3}) == 1
   * ArgMax.of({4, 3, 2, 4, 3}) == 0
   * </pre>
   * 
   * @param iterable
   * @return index of maximum entry in tensor, or -1 if tensor is empty
   * @throws Exception if given tensor is a scalar */
  public static <T> int of(Iterable<T> iterable) {
    return new ArgMax().find(iterable);
  }

  private ArgMax() {
    // ---
  }

  @Override // from IntPredicate
  public boolean test(int value) {
    return value < 0;
  }
}
