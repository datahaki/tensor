// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Abs;
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
    int windowLength = StaticHelper.default_windowLength(vector.length());
    return of(windowLength, StaticHelper.default_offset(windowLength), window).apply(vector);
  }

  /** Mathematica default
   * 
   * @param vector
   * @return
   * @throws Exception if input is not a vector */
  public static Tensor of(Tensor vector) {
    return of(vector, DirichletWindow.FUNCTION);
  }

  /** @param vector
   * @param window for instance {@link HannWindow#FUNCTION}
   * @return truncated and transposed spectrogram array for visualization
   * @throws Exception if input is not a vector */
  public static Tensor half_abs(Tensor vector, ScalarUnaryOperator window) {
    Tensor tensor = of(vector, window);
    int half = Unprotect.dimension1Hint(tensor) / 2;
    return Tensors.vector(i -> tensor.get(Tensor.ALL, half - i - 1).map(Abs.FUNCTION), half);
  }

  // ---
  /** @param windowLength
   * @param offset
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset, ScalarUnaryOperator window) {
    return new BasetrogramArray(windowLength, offset, window) {
      @Override
      protected Tensor processBlock(Tensor vector) {
        return CepstrumArray.REAL.apply(vector);
      }

      @Override
      protected String name() {
        return "CepstrogramArray";
      }
    };
  }

  /** @param windowLength
   * @param offset positive and not greater than windowLength
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset) {
    return of(windowLength, offset, DirichletWindow.FUNCTION);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param offset positive
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, int offset, ScalarUnaryOperator window) {
    return of(StaticHelper.windowLength(windowDuration, samplingFrequency), offset, window);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return spectrogram operator with default offset */
  public static TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window) {
    int windowLength = StaticHelper.windowLength(windowDuration, samplingFrequency);
    return of(windowLength, StaticHelper.default_offset(windowLength), window);
  }
}
