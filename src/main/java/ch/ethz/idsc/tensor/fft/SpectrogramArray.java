// code by ob, jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sqrt;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a>
 * 
 * @see WindowFunctions */
// LONGTERM API so that domain of frequencies is also provided
public class SpectrogramArray implements TensorUnaryOperator {
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  /** @param vector
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return */
  public static Tensor of(Tensor vector, ScalarUnaryOperator window) {
    int num = Round.intValueExact(LOG2.apply(Sqrt.FUNCTION.apply(RealScalar.of(vector.length()))));
    int windowLength = 1 << (num + 1);
    return of(windowLength, default_offset(windowLength), window).apply(vector);
  }

  /** Mathematica default
   * 
   * @param vector
   * @return
   * @throws Exception if input is not a vector */
  public static Tensor of(Tensor vector) {
    return of(vector, DirichletWindow.FUNCTION);
  }

  // helper function
  private static int default_offset(int windowLength) {
    return Round.intValueExact(RationalScalar.of(windowLength, 3));
  }

  /***************************************************/
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

  /***************************************************/
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
        .map(weights::pmul) //
        .map(tensorUnaryOperator) //
        .map(Fourier::of));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%d, %d]", getClass().getSimpleName(), windowLength, offset);
  }
}