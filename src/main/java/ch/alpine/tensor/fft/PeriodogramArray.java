// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.HammingWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PeriodogramArray.html">PeriodogramArray</a>
 * 
 * @see WindowFunctions */
public class PeriodogramArray implements TensorUnaryOperator {
  /** @param vector of length of power of 2
   * @return squared magnitude of the discrete Fourier transform (power spectrum) of given vector */
  public static Tensor of(Tensor vector) {
    return Fourier.FORWARD.transform(vector).maps(AbsSquared.FUNCTION);
  }

  /** @param vector of length of power of 2
   * @param windowLength positive
   * @return averages the power spectra of non-overlapping partitions of given windowLength */
  public static Tensor of(Tensor vector, int windowLength) {
    return of(windowLength, windowLength).apply(vector);
  }

  // ---
  /** @param windowLength
   * @param offset
   * @param window for instance {@link HammingWindow#FUNCTION}
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset, ScalarUnaryOperator window) {
    return new PeriodogramArray(windowLength, offset, window);
  }

  /** @param windowLength not smaller than offset
   * @param offset positive
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset) {
    return of(windowLength, offset, DirichletWindow.FUNCTION);
  }

  // ---
  private final TensorUnaryOperator spectrogramArray;

  private PeriodogramArray(int windowLength, int offset, ScalarUnaryOperator window) {
    spectrogramArray = new SpectrogramArray(Fourier.FORWARD::transform, windowLength, offset, window);
  }

  @Override
  public Tensor apply(Tensor vector) {
    return Mean.of(spectrogramArray.apply(vector).maps(AbsSquared.FUNCTION));
  }
}
