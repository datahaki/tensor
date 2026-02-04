// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.KroneckerDelta;
import ch.alpine.tensor.spa.SparseArray;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitVector.html">UnitVector</a> */
public enum UnitVector {
  ;
  /** UnitVector.of(n, k) == IdentityMatrix.of(n).get(k)
   * 
   * Examples:
   * <pre>
   * UnitVector.of(4, 0) == {1, 0, 0, 0}
   * UnitVector.of(4, 1) == {0, 1, 0, 0}
   * UnitVector.of(4, 3) == {0, 0, 0, 1}
   * </pre>
   * 
   * @param length positive
   * @param k_th non-negative and less than length
   * @return vector with length entries all zero, except for k-th element as 1
   * @throws Exception if input parameters are outside valid range */
  public static Tensor of(int length, int k_th) {
    if (0 <= k_th && k_th < length)
      return Tensors.vector(i -> KroneckerDelta.of(i, k_th), length);
    throw new IllegalArgumentException(length + " k_th=" + k_th);
  }

  /** @param length
   * @param k_th
   * @return */
  public static Tensor sparse(int length, int k_th) {
    Tensor vector = SparseArray.of(RealScalar.ZERO, length);
    vector.set(RealScalar.ONE, k_th);
    return vector;
  }
}
