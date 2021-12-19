// code by jph
package ch.alpine.tensor.red;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.IntPredicate;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ abstract class ArgBase implements IntPredicate {
  /** -1 is the Java standard, see also {@link String#indexOf(int)} */
  public static final int EMPTY = -1;

  @SuppressWarnings("unchecked")
  public <T extends Tensor> int find(Tensor tensor, Comparator<T> comparator) {
    if (Tensors.isEmpty(tensor))
      return EMPTY;
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor ref = iterator.next();
    int arg = 0;
    int index = 1;
    while (iterator.hasNext()) {
      Tensor cmp = iterator.next();
      if (test(comparator.compare((T) ref, (T) cmp))) {
        ref = cmp;
        arg = index;
      }
      ++index;
    }
    return arg;
  }

  @SuppressWarnings("unchecked")
  public <T extends Comparable<T>> int find(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return EMPTY;
    Iterator<Tensor> iterator = tensor.iterator();
    T ref = (T) iterator.next();
    int arg = 0;
    int index = 1;
    while (iterator.hasNext()) {
      T cmp = (T) iterator.next();
      if (test(ref.compareTo(cmp))) {
        ref = cmp;
        arg = index;
      }
      ++index;
    }
    return arg;
  }
}
