// code by gjoel and jph
package ch.alpine.tensor.pdf;

import java.util.NavigableMap;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BinCounts.html">BinCounts</a> */
public enum BinCounts {
  ;
  /** counts elements in the intervals:
   * [0, 1) [1, 2) [2, 3) ...
   * 
   * @param vector of non-negative scalars
   * @return
   * @throws Exception if any scalar in the given vector is less than zero */
  public static Tensor of(Tensor vector) {
    if (Tensors.isEmpty(vector))
      return Tensors.empty();
    NavigableMap<Tensor, Long> navigableMap = Tally.sorted(Floor.of(vector));
    Sign.requirePositiveOrZero((Scalar) navigableMap.firstKey());
    int length = Scalars.intValueExact((Scalar) navigableMap.lastKey()) + 1;
    return Tensors.vector(index -> //
    RealScalar.of(navigableMap.getOrDefault(RealScalar.of(index), 0L)), length);
  }

  /** counts elements in the intervals:
   * [0, width) [width 2*width) [2*width 3*width) ...
   * 
   * Example:
   * BinCounts.of(Tensors.vector(6, 7, 1, 2, 3, 4, 2), RealScalar.of(2)) == {1, 3, 1, 2}
   * 
   * @param vector of non-negative scalars
   * @param width of a single bin, strictly positive number
   * @return
   * @throws Exception if any scalar in the given vector is less than zero */
  public static Tensor of(Tensor vector, Scalar width) {
    return of(vector.divide(Sign.requirePositive(width)));
  }
}
