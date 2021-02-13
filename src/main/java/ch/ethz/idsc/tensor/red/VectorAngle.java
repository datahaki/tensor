// code by jph
package ch.ethz.idsc.tensor.red;

import java.util.Optional;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.nrm.VectorNorm2;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Conjugate;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/VectorAngle.html">VectorAngle</a> */
public enum VectorAngle {
  ;
  /** @param u vector
   * @param v vector of same length as u
   * @return angle between the vectors u and v, or empty if either norm of u or v is zero
   * @throws Exception if u and v are not vectors of the same length */
  public static Optional<Scalar> of(Tensor u, Tensor v) {
    Scalar nu = VectorNorm2.of(u);
    Scalar nv = VectorNorm2.of(v);
    if (Scalars.isZero(nu) || Scalars.isZero(nv)) {
      if (u.length() != v.length())
        throw TensorRuntimeException.of(u, v);
      return Optional.empty();
    }
    Scalar ratio = ExactTensorQ.of(u) || ExactTensorQ.of(v) //
        ? (Scalar) u.dot(Conjugate.of(v)).divide(nu).divide(nv)
        : (Scalar) VectorNorm2.NORMALIZE.apply(u).dot(VectorNorm2.NORMALIZE.apply(Conjugate.of(v)));
    if (ratio instanceof RealScalar)
      // due to numerical inaccuracy, for instance, ratio == 1.0000000000000002 may occur
      ratio = Clips.absoluteOne().apply(ratio); // clip to [-1, 1]
    return Optional.of(ArcCos.FUNCTION.apply(ratio));
  }
}
