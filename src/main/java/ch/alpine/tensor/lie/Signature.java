// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.ext.Integers;

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
        ? SIGN[Integers.parity(Ordering.INCREASING.of(tensor))]
        : RealScalar.ZERO;
  }
}
