// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Ordering;

/** Examples:
 * <pre>
 * Signature[{0, 1, 2}] == +1
 * Signature[{1, 0, 2}] == -1
 * Signature[{0, 0, 2}] == 0
 * </pre>
 * 
 * <pre>
 * Signature[{}] == +1
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Signature.html">Signature</a>
 * 
 * @see Ordering */
public enum Signature {
  ;
  private static final Scalar[] SIGN = new Scalar[] { //
      RealScalar.ONE, //
      RealScalar.ONE.negate() };

  /** @param tensor not a scalar
   * @return either +1 or -1, or zero if given vector contains duplicate values
   * @throws Exception if given tensor is a scalar */
  public static Scalar of(Tensor tensor) {
    long count = tensor.stream().distinct().count();
    return tensor.length() == count //
        ? of(Ordering.INCREASING.of(tensor))
        : RealScalar.ZERO;
  }

  /** Careful:
   * function assumes that given ordering is a permutation of range [0, 1, ..., n - 1].
   * for other input an infinite loop might occur!
   * 
   * @param ordering
   * @return */
  /* package */ static Scalar of(int[] ordering) {
    int parity = 0;
    for (int index = 0; index < ordering.length; ++index)
      while (ordering[index] != index) {
        int value = ordering[index];
        ordering[index] = ordering[value];
        ordering[value] = value;
        parity ^= 1;
      }
    return SIGN[parity];
  }
}
