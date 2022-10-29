// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.num.Boole;

public enum TsOp {
  ;
  /** the function can be used in combination with integration
   * to determine the extent during which the predicate is true
   * Example:
   * <pre>
   * TimeSeries timeSeries = TimeSeriesOp.indicator(input, Sign::isPositive);
   * Tensor result = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
   * </pre>
   * 
   * @param timeSeries
   * @param predicate for testing each value in given time series
   * @return time series with scalar values of either 0 or 1 and resampling method
   * depending on the evaluation of the predicate at each value, and with
   * {@link ResamplingMethods#HOLD_VALUE_FROM_LEFT_SPARSE} */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> TimeSeries indicator(TimeSeries timeSeries, Predicate<T> predicate) {
    return TimeSeries.of(timeSeries.stream() //
        .map(entry -> new TsEntry(entry.key(), Boole.of(predicate.test((T) entry.value())))), //
        ResamplingMethods.HOLD_VALUE_FROM_LEFT_SPARSE);
  }

  /** Example:
   * for the set {4, 10, 12, 20} the time series with path
   * <pre>
   * {{4, 0}, {10, 1}, {12, 2}, {20, 3}}
   * </pre>
   * will be generated.
   * 
   * @param navigableSet
   * @return time series with domain setcover[navigableSet] and integer values
   * incrementing by one for each element in the set and resampling method
   * {@link ResamplingMethods#HOLD_VALUE_FROM_LEFT} */
  public static TimeSeries indicator(NavigableSet<Scalar> navigableSet) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return TimeSeries.of(navigableSet.stream() //
        .map(key -> new TsEntry(key, RealScalar.of(atomicInteger.getAndIncrement()))), //
        ResamplingMethods.HOLD_VALUE_FROM_LEFT);
  }
}
