// code by ob, jph
package ch.alpine.tensor.fft;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a>
 * 
 * @see WindowFunctions */
public class SpectrogramArray implements TensorUnaryOperator {
  public static final SpectrogramArray SPECTROGRAM = new SpectrogramArray(Fourier.FORWARD::transform);
  // ---
  private final TensorUnaryOperator process;
  private final SlidingWindow slidingWindow;
  private final ScalarUnaryOperator window;

  // TODO TENSOR need to document input parameters, e.g. windowLength > 0 or null
  /** @param process
   * @param windowLength
   * @param offset positive and not greater than windowLength, or null
   * @param window */
  public SpectrogramArray( //
      TensorUnaryOperator process, //
      Integer windowLength, //
      Integer offset, //
      ScalarUnaryOperator window) {
    this.process = Objects.requireNonNull(process);
    slidingWindow = new SlidingWindow(windowLength, offset);
    this.window = window;
  }

  public SpectrogramArray( //
      TensorUnaryOperator process, //
      SlidingWindow samplingFrequency, //
      ScalarUnaryOperator window) {
    this.process = Objects.requireNonNull(process);
    this.slidingWindow = Objects.requireNonNull(samplingFrequency);
    this.window = window;
  }

  /** @param process for instance Fourier.FORWARD::transform */
  public SpectrogramArray(TensorUnaryOperator process) {
    this(process, null, null, null);
  }

  /** @param vector
   * @param window for instance {@link HannWindow#FUNCTION}
   * @return truncated and transposed spectrogram array for visualization
   * @throws Exception if input is not a vector */
  public final Tensor half_abs(Tensor vector) {
    Tensor tensor = apply(vector);
    int half = Unprotect.dimension1Hint(tensor) / 2;
    return Tensors.vector(i -> tensor.get(Tensor.ALL, half - i - 1).map(Abs.FUNCTION), half);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param offset positive
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public final TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, int offset, ScalarUnaryOperator window) {
    return new SpectrogramArray(process, SlidingWindow.of(windowDuration, samplingFrequency, offset), window);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return spectrogram operator with default offset */
  public final TensorUnaryOperator of(Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window) {
    return new SpectrogramArray(process, SlidingWindow.of(windowDuration, samplingFrequency, null), window);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor vector) {
    return Tensor.of(slidingWindow.complete(vector.length()).stream(vector, window).map(process));
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("SpectrogramArray", process);
  }
}
