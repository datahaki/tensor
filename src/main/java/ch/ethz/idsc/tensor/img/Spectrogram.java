// code by jph
package ch.ethz.idsc.tensor.img;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.fft.SpectrogramArray;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;

/** Mathematica::Spectrogram has the option to multiply the data segments with a window function,
 * with {@link DirichletWindow} as the default. The implementation in the tensor library uses the fixed
 * choice of {@link DirichletWindow}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Spectrogram.html">Spectrogram</a> */
public enum Spectrogram {
  ;
  /** Example:
   * <pre>
   * Spectrogram.of(vector, ColorDataGradients.VISIBLESPECTRUM);
   * </pre>
   * 
   * @param vector
   * @param function for instance {@link ColorDataGradients#VISIBLESPECTRUM}
   * @return array plot of the spectrogram of given vector with colors specified by given function
   * @throws Exception if input is not a vector */
  public static Tensor of(Tensor vector, ScalarUnaryOperator window, Function<Scalar, ? extends Tensor> function) {
    return ArrayPlot.of(array(vector, window), function);
  }

  /** @param vector
   * @return truncated and transposed {@link SpectrogramArray} for visualization
   * @throws Exception if input is not a vector */
  public static Tensor array(Tensor vector, ScalarUnaryOperator window) {
    Tensor tensor = SpectrogramArray.of(vector, window);
    int half = Unprotect.dimension1(tensor) / 2;
    return Tensors.vector(i -> tensor.get(Tensor.ALL, half - i - 1).map(Abs.FUNCTION), half);
  }
}
