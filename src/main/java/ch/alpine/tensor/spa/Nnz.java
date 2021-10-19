// code by jph
package ch.alpine.tensor.spa;

import java.util.List;

import ch.alpine.tensor.Scalar;

/** matlab::nnz */
public class Nnz implements SparseEntryVisitor<Integer> {
  /** @param sparseArray
   * @return number of non-zero elements in given sparse array */
  public static int of(SparseArray sparseArray) {
    return sparseArray.visit(new Nnz());
  }

  private long count = 0;

  private Nnz() {
    // ---
  }

  @Override // from SparseEntryVisitor
  public void accept(List<Integer> list, Scalar scalar) {
    ++count;
  }

  @Override // from SparseEntryVisitor
  public Integer result() {
    return Math.toIntExact(count);
  }
}
