// code by jph
package ch.alpine.tensor.tmp;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeriesRescale.html">TimeSeriesRescale</a> */
public class TimeSeriesAggregate {
  /** @param binaryOperator
   * @param resamplingMethod
   * @return */
  public static TimeSeriesAggregate of(BinaryOperator<Tensor> binaryOperator, ResamplingMethod resamplingMethod) {
    return new TimeSeriesAggregate( //
        Objects.requireNonNull(binaryOperator), //
        Objects.requireNonNull(resamplingMethod));
  }

  // ---
  private final BinaryOperator<Tensor> binaryOperator;
  private final ResamplingMethod resamplingMethod;

  private TimeSeriesAggregate(BinaryOperator<Tensor> binaryOperator, ResamplingMethod resamplingMethod) {
    this.binaryOperator = binaryOperator;
    this.resamplingMethod = resamplingMethod;
  }

  public TimeSeries of(TimeSeries timeSeries, Scalar offset, Scalar delta) {
    Sign.requirePositive(delta);
    TimeSeries result = TimeSeries.empty(resamplingMethod);
    Clip domain = timeSeries.domain();
    while (Scalars.lessThan(offset, domain.max())) {
      Clip clip = Clips.interval(offset, offset.add(delta));
      Optional<Tensor> optional = timeSeries.block(clip, clip.max().equals(domain.max())).stream() //
          .map(TsEntry::value) //
          .reduce(binaryOperator);
      if (optional.isPresent())
        result.insert(clip.min(), optional.orElseThrow());
      offset = clip.max();
    }
    return result;
  }
}
