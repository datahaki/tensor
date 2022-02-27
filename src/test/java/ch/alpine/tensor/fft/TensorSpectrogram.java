// code by jph
package ch.alpine.tensor.fft;

import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Spectrogram.html">Spectrogram</a>
 * 
 * @see WindowFunctions */
public enum TensorSpectrogram {
  ;
  /** Example:
   * <pre>
   * Spectrogram.of(vector, HannWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM);
   * </pre>
   * 
   * @param vector
   * @param window for instance {@link HannWindow#FUNCTION}
   * @param function for instance {@link ColorDataGradients#VISIBLE_SPECTRUM}
   * @return array plot of the spectrogram of given vector with colors specified by given function
   * @throws Exception if input is not a vector */
  public static Tensor of(Tensor vector, ScalarUnaryOperator window, Function<Scalar, ? extends Tensor> function) {
    return Raster.of(SpectrogramArray.half_abs(vector, window), function);
  }
}
