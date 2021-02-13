// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sqrt;

public enum VectorNorm2 {
  ;
  public static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorNorm2::of);

  /** @param vector
   * @return 2-norm of given vector */
  public static Scalar of(Tensor vector) {
    try {
      // Hypot prevents the incorrect evaluation: Norm_2[ {1e-300, 1e-300} ] == 0
      return Hypot.ofVector(vector);
    } catch (Exception exception) {
      // <- when vector is a scalar
      // <- when vector is empty, or contains NaN
    }
    return Sqrt.FUNCTION.apply(VectorNorm2Squared.of(vector));
  }

  /** @param v1 vector
   * @param v2 vector
   * @return 2-norm of vector difference || v1 - v2 || */
  public static Scalar between(Tensor v1, Tensor v2) {
    return of(v1.subtract(v2));
  }
}
