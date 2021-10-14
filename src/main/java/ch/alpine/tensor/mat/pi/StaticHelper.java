// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.qty.QuantityUnit;

/* package */ enum StaticHelper {
  ;
  /** @param matrix
   * @return whether scalar entries have mixed units */
  public static boolean isMixedUnits(Tensor matrix) {
    return matrix.flatten(1) //
        .map(Scalar.class::cast) //
        .map(QuantityUnit::of) //
        .distinct() //
        .skip(1) //
        .findAny() //
        .isPresent();
  }
}
