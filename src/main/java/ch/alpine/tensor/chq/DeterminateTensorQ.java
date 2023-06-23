// code by jph
package ch.alpine.tensor.chq;

import java.util.Optional;
import java.util.function.Predicate;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;

/** @see DeterminateScalarQ */
public enum DeterminateTensorQ {
  ;
  /** @param tensor
   * @return whether all scalars in tensor satisfy {@link DeterminateScalarQ} */
  public static boolean of(Tensor tensor) {
    return tensor.flatten(-1).map(Scalar.class::cast).allMatch(DeterminateScalarQ::of);
  }

  /** @param tensor
   * @return given tensor
   * @throws Exception if not all scalar entries in given tensor satisfy {@link DeterminateScalarQ} */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    Optional<Scalar> optional = tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .filter(Predicate.not(DeterminateScalarQ::of)) //
        .findFirst();
    throw new Throw(tensor, optional.orElseThrow());
  }
}
