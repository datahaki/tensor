// code by jph
package ch.alpine.tensor.spa;

import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.Scalar;

/* package */ class SparseArrayToString implements SparseEntryVisitor<String> {
  private final List<String> result = new LinkedList<>();

  @Override
  public void accept(List<Integer> list, Scalar scalar) {
    result.add("@" + list.toString() + "=" + scalar);
  }

  @Override
  public String supply() {
    return SparseArray.class.getSimpleName() + result.toString();
  }
}
