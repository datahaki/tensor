// code by jph
package ch.alpine.tensor.fft;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;

/** if windowLength is not a power of 2, the window will automatically padded with zeros
 * 
 * @param windowLength positive, or null
 * @param offset positive and not greater than windowLength, or null */
public record SlidingWindow(Integer windowLength, Integer offset) implements Serializable {
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  public SlidingWindow {
    if (Objects.nonNull(windowLength))
      Integers.requirePositive(windowLength);
    if (Objects.nonNull(offset)) {
      Integers.requirePositive(offset);
      if (Objects.nonNull(windowLength) && windowLength < offset)
        throw new IllegalArgumentException("windowLength=" + windowLength + " offset=" + offset);
    }
  }

  /** @param vector_length
   * @return new instance of sliding window where windowLength and offset are non-null */
  public SlidingWindow complete(int vector_length) {
    int windowLength = Objects.isNull(this.windowLength) //
        ? 1 << (Round.intValueExact(LOG2.apply(Sqrt.FUNCTION.apply(RealScalar.of(vector_length)))) + 1)
        : this.windowLength;
    int offset = Objects.isNull(this.offset) //
        ? Round.intValueExact(Rational.of(windowLength, 3))
        : this.offset;
    return new SlidingWindow(windowLength, offset);
  }

  /** @param window may be null
   * @return */
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

  /** @param vector
   * @param window may be null
   * @return stream of extracts of given vector pre-multiplied by window function */
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
    Tensor weights = samples.maps(window);
    return weights.multiply(RealScalar.of(length).divide(Total.ofVector(weights)));
  }

  /** @param length
   * @return vector of given length */
  @PackageTestAccess
  static Tensor samples(int length) {
    int k = length - 1;
    return Subdivide.increasing(Clips.absolute(Rational.of(k, length + length)), k);
  }
}
