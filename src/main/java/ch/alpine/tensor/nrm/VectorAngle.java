// code by jph
package ch.alpine.tensor.nrm;

import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.tri.ArcCos;

/** Remark:
 * In the special case that either vector has norm 0, the return values are
 * Mathematica::VectorAngle[{0, 0}, {a, b}] == Indeterminate
 * Tensor-Lib.::VectorAngle[{0, 0}, {a, b}] == Optional.empty()
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/VectorAngle.html">VectorAngle</a> */
public enum VectorAngle {
  ;
  /** @param u vector
   * @param v vector of same length as u
   * @return angle between the vectors u and v in the range [0, pi],
   * or empty if either norm of u or v is zero
   * @throws Exception if u and v are not vectors of the same length */
  public static Optional<Scalar> of(Tensor u, Tensor v) {
    return StaticHelper.ratio(u, v) //
        .map(ArcCos.FUNCTION);
  }
}
