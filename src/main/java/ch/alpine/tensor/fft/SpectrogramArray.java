// code by ob, jph
package ch.alpine.tensor.fft;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.red.Times;
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
  private final TensorUnaryOperator process;
  private final Integer windowLength;
  private final Integer offset;
  private final ScalarUnaryOperator window;

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
    this.windowLength = windowLength;
    this.offset = offset;
    this.window = window;
  }

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
    return new SpectrogramArray(process, StaticHelper.windowLength(windowDuration, samplingFrequency), offset, window);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return spectrogram operator with default offset */
  public final TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window) {
    int windowLength = StaticHelper.windowLength(windowDuration, samplingFrequency);
    return new SpectrogramArray(process, windowLength, StaticHelper.default_offset(windowLength), window);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor vector) {
    int windowLength = Objects.isNull(this.windowLength) //
        ? StaticHelper.default_windowLength(vector.length())
        : this.windowLength;
    int offset = Objects.isNull(this.offset) //
        ? StaticHelper.default_offset(windowLength)
        : this.offset;
    if (offset <= 0 || windowLength < offset)
      throw new IllegalArgumentException("windowLength=" + windowLength + " offset=" + offset);
    Scalar zero = EqualsReduce.zero(vector);
    int highestOneBit = Integer.highestOneBit(windowLength);
    TensorUnaryOperator tuo = Objects.isNull(window) //
        ? TensorUnaryOperator.IDENTITY
        : Times.operator(StaticHelper.weights(windowLength, window));
    TensorUnaryOperator padding = windowLength == highestOneBit //
        ? TensorUnaryOperator.IDENTITY //
        : PadRight.with(zero, highestOneBit * 2);
    return Tensor.of(Partition.stream(vector, windowLength, offset) //
        .map(tuo) //
        .map(padding) //
        .map(process));
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("XtrogramArray", process, windowLength);
  }
}
