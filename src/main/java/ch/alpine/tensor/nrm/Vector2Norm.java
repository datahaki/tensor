// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Euclidean norm
 * 
 * ||{a, b, c}||_2 = Sqrt[a^2 + b^2 + c^2] */
public enum Vector2Norm {
  ;
  public static final TensorUnaryOperator NORMALIZE = Normalize.with(Vector2Norm::of);

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
    return Sqrt.FUNCTION.apply(Vector2NormSquared.of(vector));
  }

  /** @param v1 vector
   * @param v2 vector
   * @return 2-norm of vector difference || v1 - v2 || */
  public static Scalar between(Tensor v1, Tensor v2) {
    return of(v1.subtract(v2));
  }
}
