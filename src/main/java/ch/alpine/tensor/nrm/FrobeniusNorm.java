// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** implementation is consistent with Mathematica
 * Norm[{3, 4}, "Frobenius"] == 5 */
public enum FrobeniusNorm {
  ;
  /** @param tensor of arbitrary rank
   * @return Frobenius norm of given tensor */
  public static Scalar of(Tensor tensor) {
    return Vector2Norm.of(Tensor.of(tensor.flatten(-1)));
  }

  /** @param t1
   * @param t2
   * @return Frobenius norm of tensor difference || t1 - t2 || */
  public static Scalar between(Tensor t1, Tensor t2) {
    return of(t1.subtract(t2));
  }
}
