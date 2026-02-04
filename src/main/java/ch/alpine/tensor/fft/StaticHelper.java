// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/* package */ enum StaticHelper {
  ;
  /** function truncates to real output depending on all-real input
   * 
   * @param vector input
   * @param result output
   * @return result output with imaginary part projected to zero, if input is all-real */
  public static Tensor re_re(Tensor vector, Tensor result) {
    return Im.allZero(vector) //
        ? result.map(Re.FUNCTION)
        : result;
  }
}
