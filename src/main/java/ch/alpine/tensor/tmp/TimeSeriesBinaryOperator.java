// code by jph
package ch.alpine.tensor.tmp;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public class TimeSeriesBinaryOperator implements BinaryOperator<TimeSeries>, Serializable {
  /** @param binaryOperator
   * @param resamplingMethod fallback may be null */
  public static BinaryOperator<TimeSeries> of( //
      BinaryOperator<Tensor> binaryOperator, //
      ResamplingMethod resamplingMethod) {
    return new TimeSeriesBinaryOperator( //
        Objects.requireNonNull(binaryOperator), //
        resamplingMethod);
  }

  // ---
  private final BinaryOperator<Tensor> binaryOperator;
  private final ResamplingMethod resamplingMethod;

  /** @param binaryOperator
   * @param resamplingMethod fallback may be null */
  private TimeSeriesBinaryOperator(BinaryOperator<Tensor> binaryOperator, ResamplingMethod resamplingMethod) {
    this.binaryOperator = Objects.requireNonNull(binaryOperator);
    this.resamplingMethod = resamplingMethod;
  }

  @Override
  public TimeSeries apply(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    ResamplingMethod resamplingMethod = timeSeries1.resamplingMethod().equals(timeSeries2.resamplingMethod()) //
        ? timeSeries1.resamplingMethod()
        : Objects.requireNonNull(this.resamplingMethod);
    Clip clip = Clips.intersection(timeSeries1.domain(), timeSeries2.domain());
    return TimeSeries.of(Stream.concat( //
        timeSeries1.keySet(clip, true).stream(), //
        timeSeries2.keySet(clip, true).stream()).distinct() //
        .map(key -> new TsEntry(key, binaryOperator.apply(timeSeries1.eval(key), timeSeries2.eval(key)))), //
        resamplingMethod);
  }
}
