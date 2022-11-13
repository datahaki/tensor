// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.red.Times;

/* package */ abstract class BasetrogramArray implements TensorUnaryOperator {
  private final int windowLength;
  private final int offset;
  private final TensorUnaryOperator tensorUnaryOperator;
  private final Tensor weights;

  public BasetrogramArray(int windowLength, int offset, ScalarUnaryOperator window) {
    if (offset <= 0 || windowLength < offset)
      throw new IllegalArgumentException("windowLength=" + windowLength + " offset=" + offset);
    // ---
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
        .map(this::processBlock));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise(title() + "Array", windowLength, offset, tensorUnaryOperator);
  }

  /** @param vector
   * @return */
  protected abstract Tensor processBlock(Tensor vector);

  /** @return */
  protected abstract String title();
}
