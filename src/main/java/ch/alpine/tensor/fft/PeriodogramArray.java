// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PeriodogramArray.html">PeriodogramArray</a>
 * 
 * @see WindowFunctions */
public record PeriodogramArray(SpectrogramArray spectrogramArray) implements TensorUnaryOperator {
  /** @param windowLength positive
   * @return averages the power spectra of non-overlapping partitions of given windowLength */
  public PeriodogramArray config(ScalarUnaryOperator window) {
    return new PeriodogramArray(spectrogramArray.config(window));
  }

  public PeriodogramArray config(Integer windowLength, Integer offset) {
    return new PeriodogramArray(spectrogramArray.config(windowLength, offset));
  }

  public PeriodogramArray config(int windowLength) {
    return new PeriodogramArray(spectrogramArray.config(windowLength, windowLength));
  }

  @Override
  public Tensor apply(Tensor vector) {
    return Mean.of(spectrogramArray.apply(vector).maps(AbsSquared.FUNCTION));
  }
}
