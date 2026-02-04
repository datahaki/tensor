// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CoefficientList.html">CoefficientList</a> */
/* package */ enum CoefficientList {
  ;
  /** The following equality holds up to permutation and numerical precision:
   * <pre>
   * Roots.of(CoefficientList.of(roots)) == roots
   * </pre>
   * 
   * @param roots vector of length 1, 2, or 3
   * @return coefficients of polynomial */
  public static Tensor of(Tensor roots) {
    return polynomial(roots).coeffs();
  }

  public static Polynomial polynomial(Tensor roots) {
    return roots.stream() //
        .map(Scalar.class::cast) //
        .map(zero -> Tensors.of(zero.negate(), zero.one())) //
        .map(Polynomial::of) //
        .reduce(Polynomial::times) //
        .orElseThrow();
  }
}
