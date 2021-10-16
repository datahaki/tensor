// code by jph
package ch.alpine.tensor.spa;

import java.util.List;

import ch.alpine.tensor.Scalar;

@FunctionalInterface
public interface SparseEntryVisitor<T> {
  /** @param list
   * @param scalar */
  void accept(List<Integer> list, Scalar scalar);

  /** function is invoked after {@link #accept(List, Scalar)} was
   * notified about all the entries in the sparse array
   * 
   * @return custom result of the visitor */
  default T supply() {
    return null;
  }
}
