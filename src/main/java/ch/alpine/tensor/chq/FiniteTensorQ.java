// code by jph
package ch.alpine.tensor.chq;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;

/** @see FiniteScalarQ */
public enum FiniteTensorQ {
  ;
  /** @param tensor
   * @return whether all scalars in tensor satisfy {@link FiniteScalarQ} */
  public static boolean of(Tensor tensor) {
    return tensor.flatten(-1).map(Scalar.class::cast).allMatch(FiniteScalarQ::of);
  }

  /** @param scalar
   * @return given scalar
   * @throws Exception if given scalar is not a finite scalar */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw Throw.of(tensor);
  }
}
