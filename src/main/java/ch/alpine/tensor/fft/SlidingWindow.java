// code by jph
package ch.alpine.tensor.fft;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;

public record SlidingWindow(Integer windowLength, Integer offset) implements Serializable {
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  /** @param windowDuration
   * @param samplingFrequency
   * @param offset
   * @return */
  public static SlidingWindow of(Scalar windowDuration, Scalar samplingFrequency, Integer offset) {
    return new SlidingWindow(Round.intValueExact(windowDuration.multiply(samplingFrequency)), offset);
  }

  /** @param vector
   * @return */
  public SlidingWindow complete(int vector_length) {
    int windowLength = Objects.isNull(this.windowLength) //
        ? 1 << (Round.intValueExact(LOG2.apply(Sqrt.FUNCTION.apply(RealScalar.of(vector_length)))) + 1)
        : this.windowLength;
    int offset = Objects.isNull(this.offset) //
        ? Round.intValueExact(RationalScalar.of(windowLength, 3))
        : this.offset;
    if (offset <= 0 || windowLength < offset)
      throw new IllegalArgumentException("windowLength=" + windowLength + " offset=" + offset);
    return new SlidingWindow(windowLength, offset);
  }

  TensorUnaryOperator tuo(ScalarUnaryOperator window) {
    return Objects.isNull(window) //
        ? t -> t // emulates DirichletWindow
        : Times.operator(weights(windowLength, window));
  }

  TensorUnaryOperator padding(Tensor vector) {
    int highestOneBit = Integer.highestOneBit(windowLength);
    return windowLength == highestOneBit //
        ? t -> t
        : PadRight.with(EqualsReduce.zero(vector), highestOneBit * 2);
  }

  public Stream<Tensor> stream(Tensor vector, ScalarUnaryOperator window) {
    return Partition.stream(vector, windowLength, offset).map(tuo(window)).map(padding(vector));
  }

  /** @param length
   * @param window
   * @return symmetric vector of given length of weights that sum up to length */
  @PackageTestAccess
  static Tensor weights(int length, ScalarUnaryOperator window) {
    Tensor samples = 1 == length //
        ? Tensors.vector(0)
        : samples(length);
    Tensor weights = samples.map(window);
    return weights.multiply(RealScalar.of(length).divide(Total.ofVector(weights)));
  }

  /** @param length
   * @return vector of given length */
  @PackageTestAccess
  static Tensor samples(int length) {
    int k = length - 1;
    return Subdivide.increasing(Clips.absolute(RationalScalar.of(k, length + length)), k);
  }
}
