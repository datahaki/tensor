// code by jph
package ch.alpine.tensor.ext;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.IntPredicate;

/* package */ abstract class ArgBase<T> implements IntPredicate {
  /** -1 is the Java standard, see also {@link String#indexOf(int)} */
  public static final int EMPTY = -1;

  /** @param iterable
   * @param comparator
   * @return */
  public int find(Iterable<T> iterable, Comparator<T> comparator) {
    Iterator<T> iterator = iterable.iterator();
    if (!iterator.hasNext())
      return EMPTY;
    T ref = iterator.next();
    int arg = 0;
    for (int index = 1; iterator.hasNext(); ++index) {
      T cmp = iterator.next();
      if (test(comparator.compare(ref, cmp))) {
        ref = cmp;
        arg = index;
      }
    }
    return arg;
  }

  /** @param iterable
   * @return */
  @SuppressWarnings("unchecked")
  public int find(Iterable<T> iterable) {
    Iterator<T> iterator = iterable.iterator();
    if (!iterator.hasNext())
      return EMPTY;
    T ref = iterator.next();
    int arg = 0;
    for (int index = 1; iterator.hasNext(); ++index) {
      T cmp = iterator.next();
      if (test(((Comparable<? super T>) ref).compareTo(cmp))) {
        ref = cmp;
        arg = index;
      }
    }
    return arg;
  }
}
