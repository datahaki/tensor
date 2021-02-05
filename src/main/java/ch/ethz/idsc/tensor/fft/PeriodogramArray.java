// code by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import ch.ethz.idsc.tensor.sca.win.HammingWindow;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PeriodogramArray.html">PeriodogramArray</a>
 * 
 * @see WindowFunctions */
// LONGTERM API so that domain of frequencies is also provided
public class PeriodogramArray implements TensorUnaryOperator {
  private static final long serialVersionUID = 3155296618510267724L;

  /** @param vector of length of power of 2
   * @return */
  public static Tensor of(Tensor vector) {
    return Fourier.of(vector).map(AbsSquared.FUNCTION);
  }

  /** @param vector of length of power of 2
   * @param windowLength positive
   * @return */
  public static Tensor of(Tensor vector, int windowLength) {
    return of(windowLength, windowLength).apply(vector);
  }

  /***************************************************/
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

  /***************************************************/
  private final TensorUnaryOperator spectrogramArray;

  private PeriodogramArray(int windowLength, int offset, ScalarUnaryOperator window) {
    spectrogramArray = SpectrogramArray.of(windowLength, offset, window);
  }

  @Override
  public Tensor apply(Tensor vector) {
    return Mean.of(spectrogramArray.apply(vector).map(AbsSquared.FUNCTION));
  }
}
