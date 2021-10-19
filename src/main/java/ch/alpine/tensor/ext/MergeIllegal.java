// code by gjoel in "uniqueKeysMapMerger()"
// adapted by jph
package ch.alpine.tensor.ext;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

/** class exists to achieve code coverage in private {@link Collector} */
public class MergeIllegal<T> implements BinaryOperator<T>, Serializable {
  /** @return new instance of a merge operator that throws an exception
   * when the function {@link #apply(Object, Object)} is called */
  public static <T> BinaryOperator<T> operator() {
    return new MergeIllegal<>();
  }

  // ---
  private MergeIllegal() {
    // ---
  }

  @Override // from BinaryOperator
  public T apply(T t, T u) {
    throw new IllegalStateException();
  }
}
