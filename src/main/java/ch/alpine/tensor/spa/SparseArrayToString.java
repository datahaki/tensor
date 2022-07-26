// code by jph
package ch.alpine.tensor.spa;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;

/** SparseArray[{{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 5}, {3, 3}] */
/* package */ class SparseArrayToString implements SparseEntryVisitor<String> {
  private static final Collector<CharSequence, ?, String> EMBRACE = Collectors.joining(", ", "{", "}");
  // ---
  private final List<String> result = new LinkedList<>();
  private final int ofs;

  /** @param ofs 0, or 1 for Mathematica index convention
   * @throws Exception if ofs is outside valid range */
  public SparseArrayToString(int ofs) {
    if (ofs < 0 || 1 < ofs)
      throw new IllegalArgumentException(Integer.toString(ofs));
    this.ofs = ofs;
  }

  @Override // from SparseEntryVisitor
  public void accept(List<Integer> list, Scalar scalar) {
    result.add(list.stream() //
        .mapToInt(i -> i + ofs) //
        .mapToObj(Integer::toString) //
        .collect(EMBRACE) + "->" + scalar);
  }

  @Override // from SparseEntryVisitor
  public String result() {
    return result.stream().collect(EMBRACE);
  }
}
