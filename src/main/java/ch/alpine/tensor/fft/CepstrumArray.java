// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.exp.Log;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CepstrumArray.html">CepstrumArray</a> */
public enum CepstrumArray {
  ;
  public static Tensor of_pwr(Tensor vector) {
    Tensor res = Fourier.of(vector).map(AbsSquared.FUNCTION).map(Log.FUNCTION);
    return InverseFourier.of(res).map(AbsSquared.FUNCTION);
  }

  public static Tensor of_real(Tensor vector) {
    Tensor res = Fourier.of(vector).map(Abs.FUNCTION).map(Log.FUNCTION);
    return InverseFourier.of(res).map(Re.FUNCTION);
  }
}
