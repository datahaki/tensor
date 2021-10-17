// code by jph
package ch.alpine.tensor.spa;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;

/** SparseArray[{{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 5}, {3, 3}] */
/* package */ class SparseArrayToString implements SparseEntryVisitor<String> {
  private static final Collector<CharSequence, ?, String> EMBRACE = Collectors.joining(", ", "{", "}");
  // ---
  private final List<String> result = new LinkedList<>();

  @Override // from SparseEntryVisitor
  public void accept(List<Integer> list, Scalar scalar) {
    result.add(Tensors.vector(list) + "->" + scalar);
  }

  @Override // from SparseEntryVisitor
  public String result() {
    return result.stream().collect(EMBRACE);
  }
}
