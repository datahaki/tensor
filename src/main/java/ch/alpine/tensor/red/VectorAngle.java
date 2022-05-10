// code by jph
package ch.alpine.tensor.red;

import java.util.Optional;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.tri.ArcCos;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/VectorAngle.html">VectorAngle</a> */
public enum VectorAngle {
  ;
  /** @param u vector
   * @param v vector of same length as u
   * @return angle between the vectors u and v in the range [0, pi],
   * or empty if either norm of u or v is zero
   * @throws Exception if u and v are not vectors of the same length */
  public static Optional<Scalar> of(Tensor u, Tensor v) {
    Scalar nu = Vector2Norm.of(u);
    Scalar nv = Vector2Norm.of(v);
    if (Scalars.isZero(nu) || Scalars.isZero(nv)) {
      Integers.requireEquals(u.length(), v.length());
      return Optional.empty();
    }
    Scalar ratio = ExactTensorQ.of(u) || ExactTensorQ.of(v) //
        ? (Scalar) u.dot(Conjugate.of(v)).divide(nu).divide(nv)
        : (Scalar) Vector2Norm.NORMALIZE.apply(u).dot(Vector2Norm.NORMALIZE.apply(Conjugate.of(v)));
    if (ratio instanceof RealScalar)
      // due to numerical inaccuracy, for instance, ratio == 1.0000000000000002 may occur
      ratio = Clips.absoluteOne().apply(ratio); // clip to [-1, 1]
    return Optional.of(ArcCos.FUNCTION.apply(ratio));
  }
}
