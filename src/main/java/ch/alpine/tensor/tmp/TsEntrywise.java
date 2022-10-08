// code by jph
package ch.alpine.tensor.tmp;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.red.Inner;

/** @see Entrywise */
public enum TsEntrywise {
  ;
  private static final BinaryOperator<TimeSeries> TIMES = TimeSeriesBinaryOperator.of( //
      Inner.with((s, t) -> t.multiply(s)), ResamplingMethods.LINEAR);

  /** implementation is consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the sum of the two given time series */
  public static TimeSeries plus(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return TimeSeriesBinaryOperator.of(Tensor::add, ResamplingMethods.LINEAR) //
        .apply(timeSeries1, timeSeries2);
  }

  /** implementation is consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the timeSeries1 minus timeSeries2 */
  public static TimeSeries minus(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return TimeSeriesBinaryOperator.of(Tensor::subtract, ResamplingMethods.LINEAR) //
        .apply(timeSeries1, timeSeries2);
  }

  /** implementation is consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the (approximate) pointwise product of the two given time series */
  public static TimeSeries times(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return TIMES.apply(timeSeries1, timeSeries2);
  }

  /** @param timeSeries1
   * @param timeSeries2
   * @return */
  public static TimeSeries min(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return TimeSeriesBinaryOperator.of(Entrywise.min(), ResamplingMethods.LINEAR) //
        .apply(timeSeries1, timeSeries2);
  }

  /** @param timeSeries1
   * @param timeSeries2
   * @return */
  public static TimeSeries max(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return TimeSeriesBinaryOperator.of(Entrywise.max(), ResamplingMethods.LINEAR) //
        .apply(timeSeries1, timeSeries2);
  }

  /** @param timeSeries
   * @param factor
   * @return */
  public static TimeSeries multiply(TimeSeries timeSeries, Scalar factor) {
    return map(timeSeries, factor::multiply);
  }

  /** @param timeSeries
   * @param function
   * @return */
  public static TimeSeries map(TimeSeries timeSeries, Function<Scalar, ? extends Tensor> function) {
    return map(timeSeries, function, timeSeries.resamplingMethod());
  }

  /** @param timeSeries
   * @param function
   * @param resamplingMethod
   * @return */
  public static TimeSeries map(TimeSeries timeSeries, Function<Scalar, ? extends Tensor> function, ResamplingMethod resamplingMethod) {
    return TimeSeries.of(timeSeries.stream() //
        .map(entry -> new TsEntry(entry.key(), entry.value().map(function))), //
        resamplingMethod);
  }
}
