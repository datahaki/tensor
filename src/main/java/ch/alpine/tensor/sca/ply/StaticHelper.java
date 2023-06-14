// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;

/* package */ enum StaticHelper {
  ;
  /** @return unit of domain
   * @throws Exception if {@link #coeffs} has length 1 */
  public static Unit getDomainUnit(Tensor coeffs) {
    Scalar a = coeffs.Get(0).zero(); // zero() is required for DateTime
    Scalar b = coeffs.Get(1);
    return QuantityUnit.of(a).add(QuantityUnit.of(b).negate()); // of domain
  }
}
