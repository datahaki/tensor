// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;

/** implementation is consistent with Mathematica
 * Norm[{3, 4}, "Frobenius"] == 5
 * 
 * FrobeniusNorm[matrix] == Vector2Norm[SVD[matrix].values()] */
public enum FrobeniusNorm {
  ;
  /** @param tensor of arbitrary rank
   * @return Frobenius norm of given tensor */
  public static Scalar of(Tensor tensor) {
    return Vector2Norm.of(Flatten.of(tensor));
  }

  /** @param tensor of arbitrary rank
   * @return Frobenius norm squared of given tensor */
  public static Scalar squared(Tensor tensor) {
    return Vector2NormSquared.of(Flatten.of(tensor));
  }
}
