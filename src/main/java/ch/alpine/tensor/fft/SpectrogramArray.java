// code by ob, jph
package ch.alpine.tensor.fft;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a>
 * 
 * @see WindowFunctions */
public interface SpectrogramArray extends TensorUnaryOperator {
  SpectrogramArray SPECTROGRAM = of(Fourier.FORWARD::transform);

  /** @param process
   * @param windowLength
   * @param offset positive and not greater than windowLength, or null
   * @param window */
  static SpectrogramArray of( //
      TensorUnaryOperator process, Integer windowLength, Integer offset, ScalarUnaryOperator window) {
    return new SpectrogramArrayImpl( //
        Objects.requireNonNull(process), //
        new SlidingWindow(windowLength, offset), //
        window);
  }

  /** @param process for instance Fourier.FORWARD::transform */
  static SpectrogramArray of(TensorUnaryOperator process) {
    return of(process, null, null, null);
  }

  /** @param vector
   * @param window for instance {@link HannWindow#FUNCTION}
   * @return truncated and transposed spectrogram array for visualization
   * @throws Exception if input is not a vector */
  Tensor half_abs(Tensor vector);

  /** @param windowDuration
   * @param samplingFrequency
   * @param offset positive
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, int offset, ScalarUnaryOperator window);

  /** @param windowDuration
   * @param samplingFrequency
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return spectrogram operator with default offset */
  TensorUnaryOperator of(Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window);
}
