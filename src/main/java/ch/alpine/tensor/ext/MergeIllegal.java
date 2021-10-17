// code by jph
package ch.alpine.tensor.ext;

import java.io.Serializable;
import java.util.function.BinaryOperator;

/** the idea was also implemented by gjoel in "uniqueKeysMapMerger()" */
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
