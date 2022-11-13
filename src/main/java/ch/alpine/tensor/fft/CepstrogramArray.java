// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CepstrogramArray.html">CepstrogramArray</a>
 * 
 * @see WindowFunctions */
public enum CepstrogramArray {
  ;
  /** @param vector
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static Tensor of(Tensor vector, ScalarUnaryOperator window) {
    return XtrogramArray.CepstrogramReal.of(vector, window);
  }

  /** Mathematica default
   * 
   * @param vector
   * @return
   * @throws Exception if input is not a vector */
  public static Tensor of(Tensor vector) {
    return XtrogramArray.CepstrogramReal.of(vector);
  }

  /** @param vector
   * @param window for instance {@link HannWindow#FUNCTION}
   * @return truncated and transposed spectrogram array for visualization
   * @throws Exception if input is not a vector */
  public static Tensor half_abs(Tensor vector, ScalarUnaryOperator window) {
    return XtrogramArray.CepstrogramReal.half_abs(vector, window);
  }

  // ---
  /** @param windowLength
   * @param offset
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset, ScalarUnaryOperator window) {
    return XtrogramArray.CepstrogramReal.of(windowLength, offset, window);
  }

  /** @param windowLength
   * @param offset positive and not greater than windowLength
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset) {
    return XtrogramArray.CepstrogramReal.of(windowLength, offset);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param offset positive
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, int offset, ScalarUnaryOperator window) {
    return XtrogramArray.CepstrogramReal.of(windowDuration, samplingFrequency, offset, window);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return spectrogram operator with default offset */
  public static TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window) {
    return XtrogramArray.CepstrogramReal.of(windowDuration, samplingFrequency, window);
  }
}
