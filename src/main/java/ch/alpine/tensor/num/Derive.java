// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;

public enum Derive {
  ;
  /** Hint:
   * ordering of coefficients is <em>reversed</em> compared to
   * MATLAB::polyval, MATLAB::polyfit, etc. !
   * 
   * Example:
   * <pre>
   * Derive[{a, b, c, d}] == {b, 2*c, 3*d}
   * </pre>
   * 
   * @param coeffs
   * @return coefficients of polynomial that is the derivative of the polynomial defined by given coeffs
   * @throws Exception if given coeffs is not a vector */
  public static Tensor of(Tensor coeffs) {
    int length = coeffs.length();
    return length == 0 //
        ? Tensors.empty()
        : coeffs.extract(1, length).pmul(Range.of(1, length));
  }
}
