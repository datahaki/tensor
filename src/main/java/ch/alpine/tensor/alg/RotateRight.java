// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RotateRight.html">RotateRight</a>
 * 
 * @see RotateLeft */
public enum RotateRight {
  ;
  /** <pre>
   * RotateRight[{a, b, c, d, e}, 2] == {d, e, a, b, c}
   * RotateRight[{}, n] == {}
   * </pre>
   * 
   * @param tensor
   * @param n any integer
   * @return
   * @throws Exception if given tensor is a {@link Scalar} */
  public static Tensor of(Tensor tensor, int n) {
    return RotateLeft.of(tensor, -n);
  }
}
