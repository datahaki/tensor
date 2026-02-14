// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;

// TODO name of class is no good
public enum EqualsReduce {
  ;
  /** @param tensor
   * @return common {@link Scalar#zero()} of all entries in given tensor
   * with precedence of inexact precision over exact precision */
  public static Scalar zero(Tensor tensor) {
    return Flatten.scalars(tensor) //
        .map(Scalar::zero) //
        .reduce(Scalar::add) //
        .orElseThrow();
  }
}
