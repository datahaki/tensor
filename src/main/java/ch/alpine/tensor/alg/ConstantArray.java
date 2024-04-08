// code by jph
package ch.alpine.tensor.alg;

import java.util.Collections;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;

/** MATLAB::repmat
 * 
 * <p>Implementation produces an unmodifiable tensor of array structure with all
 * entries identical. The representation has low memory footprint, i.e. O(rank).
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ConstantArray.html">ConstantArray</a>
 * 
 * @see Array */
public enum ConstantArray {
  ;
  /** @param entry non-null
   * @param dimensions
   * @return unmodifiable tensor with given dimensions and entries as given entry */
  public static Tensor of(Tensor entry, List<Integer> dimensions) {
    Tensor tensor = entry.copy();
    for (int index : dimensions.reversed())
      tensor = Unprotect.using(Collections.nCopies(index, tensor));
    return tensor.unmodifiable();
  }

  /** @param entry non-null
   * @param dimensions
   * @return unmodifiable tensor with given dimensions and entries as given entry */
  public static Tensor of(Tensor entry, int... dimensions) {
    return of(entry, Integers.asList(dimensions));
  }
}
