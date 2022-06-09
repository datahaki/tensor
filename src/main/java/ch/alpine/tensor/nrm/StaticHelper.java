// code by jph
package ch.alpine.tensor.nrm;

import java.util.Optional;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Conjugate;

/* package */ enum StaticHelper {
  ;
  /** @param u
   * @param v
   * @return u . v / (|u| |v|) */
  public static Optional<Scalar> ratio(Tensor u, Tensor v) {
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
    return Optional.of(ratio);
  }
}
