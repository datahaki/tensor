// code by jph
package ch.alpine.tensor.alg;

import java.util.List;

import ch.alpine.tensor.Tensors;

/** utility class for {@link Transpose} */
/* package */ class MultiIndex {
  /** the content of size[] is not changed after construction */
  /* package */ final int[] size;

  private MultiIndex(int[] dims) {
    this.size = dims;
  }

  public MultiIndex(List<Integer> list) {
    this(list.stream().mapToInt(Integer::intValue).toArray());
  }

  @Override // from Object
  public String toString() {
    return Tensors.vectorInt(size).toString();
  }
}
