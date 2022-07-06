// code by ob, jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a>
 * 
 * @see WindowFunctions */
public class SpectrogramArray implements TensorUnaryOperator {
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  /** @param vector
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static Tensor of(Tensor vector, ScalarUnaryOperator window) {
    int windowLength = default_windowLength(vector.length());
    return of(windowLength, default_offset(windowLength), window).apply(vector);
  }

  /** @param vector_length
   * @return power of 2 */
  private static int default_windowLength(int vector_length) {
    int num = Round.intValueExact(LOG2.apply(Sqrt.FUNCTION.apply(RealScalar.of(vector_length))));
    return 1 << (num + 1);
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

  // helper function
  private static int default_offset(int windowLength) {
    return Round.intValueExact(RationalScalar.of(windowLength, 3));
  }

  // ---
  /** @param windowLength
   * @param offset
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset, ScalarUnaryOperator window) {
    if (offset <= 0 || windowLength < offset)
      throw new IllegalArgumentException("windowLength=" + windowLength + " offset=" + offset);
    return new SpectrogramArray(windowLength, offset, window);
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
    return of(windowLength(windowDuration, samplingFrequency), offset, window);
  }

  /** @param windowDuration
   * @param samplingFrequency
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return spectrogram operator with default offset */
  public static TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window) {
    int windowLength = windowLength(windowDuration, samplingFrequency);
    return of(windowLength, default_offset(windowLength), window);
  }

  // helper function
  private static int windowLength(Scalar windowDuration, Scalar samplingFrequency) {
    return Round.intValueExact(windowDuration.multiply(samplingFrequency));
  }

  // ---
  private final int windowLength;
  private final int offset;
  private final TensorUnaryOperator tensorUnaryOperator;
  private final Tensor weights;

  private SpectrogramArray(int windowLength, int offset, ScalarUnaryOperator window) {
    this.windowLength = windowLength;
    this.offset = offset;
    int highestOneBit = Integer.highestOneBit(windowLength);
    weights = StaticHelper.weights(windowLength, window);
    tensorUnaryOperator = windowLength == highestOneBit //
        ? t -> t //
        : PadRight.zeros(highestOneBit * 2);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor vector) {
    return Tensor.of(Partition.stream(vector, windowLength, offset) //
        .map(Times.operator(weights)) //
        .map(tensorUnaryOperator) //
        .map(Fourier::of));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("SpectrogramArray", windowLength, offset, tensorUnaryOperator);
  }
}