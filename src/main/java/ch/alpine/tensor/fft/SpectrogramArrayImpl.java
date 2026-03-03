// code by ob, jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;

/** @param process
 * @param slidingWindow
 * @param window may be null */
/* package */ record SpectrogramArrayImpl( //
    TensorUnaryOperator process, SlidingWindow slidingWindow, ScalarUnaryOperator window) implements SpectrogramArray {
  @Override
  public SpectrogramArray config(Integer windowLength, Integer offset) {
    return new SpectrogramArrayImpl(process, new SlidingWindow(windowLength, offset), window);
  }

  @Override
  public SpectrogramArray config(ScalarUnaryOperator window) {
    return new SpectrogramArrayImpl(process, slidingWindow, window);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor vector) {
    return Tensor.of(slidingWindow.complete(vector.length()).stream(vector, window).map(process));
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("SpectrogramArray", process, slidingWindow, window);
  }
}
