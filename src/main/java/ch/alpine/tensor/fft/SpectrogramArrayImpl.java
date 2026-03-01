// code by ob, jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;

/** @param process
 * @param slidingWindow
 * @param window may be null */
/* package */ record SpectrogramArrayImpl(TensorUnaryOperator process, SlidingWindow slidingWindow, ScalarUnaryOperator window) implements SpectrogramArray {
  @Override
  public TensorUnaryOperator of( //
      Scalar windowDuration, Scalar samplingFrequency, int offset, ScalarUnaryOperator window) {
    return new SpectrogramArrayImpl(process, SlidingWindow.of(windowDuration, samplingFrequency, offset), window);
  }

  @Override
  public TensorUnaryOperator of(Scalar windowDuration, Scalar samplingFrequency, ScalarUnaryOperator window) {
    return new SpectrogramArrayImpl(process, SlidingWindow.of(windowDuration, samplingFrequency, null), window);
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
