// code by jph
package ch.alpine.tensor.tmp;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** applies function to the values in time series
 *
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeriesMap.html">TimeSeriesMap</a> */
public class TimeSeriesMap implements UnaryOperator<TimeSeries>, Serializable {
  /** @param function
   * @param resamplingMethod may be null
   * @return */
  public static UnaryOperator<TimeSeries> of( //
      Function<Scalar, ? extends Tensor> function, //
      ResamplingMethod resamplingMethod) {
    return new TimeSeriesMap(Objects.requireNonNull(function), resamplingMethod);
  }

  /** Example:
   * TimeSeriesMap.of(RealScalar.of(10)::multiply).apply(timeSeries)
   * 
   * @param function
   * @return */
  public static UnaryOperator<TimeSeries> of(Function<Scalar, ? extends Tensor> function) {
    return of(function, null);
  }

  // ---
  private final Function<Scalar, ? extends Tensor> function;
  private final ResamplingMethod resamplingMethod;

  private TimeSeriesMap(Function<Scalar, ? extends Tensor> function, ResamplingMethod resamplingMethod) {
    this.function = function;
    this.resamplingMethod = resamplingMethod;
  }

  @Override
  public TimeSeries apply(TimeSeries timeSeries) {
    return TimeSeries.of(timeSeries.stream() //
        .map(entry -> new TsEntry(entry.key(), entry.value().map(function))), //
        resamplingMethod == null //
            ? timeSeries.resamplingMethod()
            : resamplingMethod);
  }
}
