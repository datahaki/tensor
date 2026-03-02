// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;

// TODO TENSOR name is not ideal
public enum EqualsReduce {
  ;
  /** @param tensor
   * @return common {@link Scalar#zero()} of all entries in given tensor
   * @throws Exception */
  public static Scalar zero(Tensor tensor) {
    return Flatten.scalars(tensor) //
        .map(Scalar::zero) //
        .reduce(Scalar::add) //
        .orElseThrow();
  }

  /** @param tensor
   * @return
   * @throws Exception if not compatible */
  public static Scalar one(Tensor tensor) {
    return Flatten.scalars(tensor) //
        .map(Scalar::one) //
        .reduce(Scalar::multiply) //
        .orElseThrow();
  }
}
