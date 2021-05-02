// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** implementation permits coefficients of type {@link GaussScalar} */
/* package */ enum RootsDegree1 {
  ;
  /** @param coeffs {a, b} representing a + b * x == 0
   * @return vector of length 1 */
  public static Tensor of(Tensor coeffs) {
    // return Tensors.of(coeffs.Get(0).divide(coeffs.Get(1)).negate());
    return Tensors.of(coeffs.Get(1).under(coeffs.Get(0)).negate());
  }
}
