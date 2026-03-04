// code by ob, jph
package ch.alpine.tensor.fft;

import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.win.HannWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a>
 * 
 * @see SpectrogramArrays
 * @see WindowFunctions */
public interface SpectrogramArray extends TensorUnaryOperator {
  /** @param process for example Fourier.FORWARD::transform */
  static SpectrogramArray of(TensorUnaryOperator process) {
    return new SpectrogramArrayImpl(Objects.requireNonNull(process), new SlidingWindow(null, null), null);
  }

  /** @param windowLength positive, or null
   * @param offset positive and not greater than windowLength, or null
   * @return */
  SpectrogramArray config(Integer windowLength, Integer offset);

  /** @param window
   * @return */
  SpectrogramArray config(ScalarUnaryOperator window);

  /** performs apply(vector), removes half of the result, entrywise abs, transpose and flip
   * particularly suitable in case vector consists entirely of real scalars
   * 
   * @param vector
   * @param window for instance {@link HannWindow#FUNCTION}
   * @return truncated and transposed spectrogram array for visualization
   * @throws Exception if input is not a vector */
  default Tensor half_abs(Tensor vector) {
    Tensor tensor = apply(vector);
    int half = Math.divideExact(Unprotect.dimension1Hint(tensor), 2);
    return Tensors.vector(i -> tensor.get(Tensor.ALL, half - i - 1).maps(Abs.FUNCTION), half);
  }
}
