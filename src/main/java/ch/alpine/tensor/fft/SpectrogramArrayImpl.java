// code by ob, jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Abs;

/** @param process
 * @param slidingWindow
 * @param window */
/* package */ record SpectrogramArrayImpl(TensorUnaryOperator process, SlidingWindow slidingWindow, ScalarUnaryOperator window) implements SpectrogramArray {
  @Override
  public Tensor half_abs(Tensor vector) {
    Tensor tensor = apply(vector);
    int half = Unprotect.dimension1Hint(tensor) / 2;
    return Tensors.vector(i -> tensor.get(Tensor.ALL, half - i - 1).maps(Abs.FUNCTION), half);
  }

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
