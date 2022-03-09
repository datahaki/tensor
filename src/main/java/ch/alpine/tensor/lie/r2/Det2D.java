// code by jph
package ch.alpine.tensor.lie.r2;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Cross;

public enum Det2D {
  ;
  /** @param p vector of length 2 with entries {px, py}
   * @param q vector of length 2 with entries {qx, qy}
   * @return px * qy - py * qx
   * @throws Exception if p or q is not a vector of length 2 */
  public static Scalar of(Tensor p, Tensor q) {
    return (Scalar) Cross.of(p).dot(q);
  }
}
