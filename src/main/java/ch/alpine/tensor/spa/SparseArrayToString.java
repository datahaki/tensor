// code by jph
package ch.alpine.tensor.spa;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;

/* package */ class SparseArrayToString implements SparseEntryVisitor<String> {
  private static final Collector<CharSequence, ?, String> COLLECTOR = Collectors.joining(",", "(", ")");
  private final List<String> result = new LinkedList<>();

  @Override // from SparseEntryVisitor
  public void accept(List<Integer> list, Scalar scalar) {
    result.add(list.stream().map(Object::toString).collect(COLLECTOR) + "=" + scalar);
  }

  @Override // from SparseEntryVisitor
  public String result() {
    return result.toString();
  }
}
