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
  public static TimeSeries indicator(NavigableSet<Scalar> navigableSet) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return TimeSeries.of(navigableSet.stream() //
        .map(key -> new TsEntry(key, RealScalar.of(atomicInteger.getAndIncrement()))), //
        ResamplingMethods.HOLD_VALUE_FROM_LEFT);
  }

  /** the function can be used in combination with integration
   * to determine the extent during which the predicate is true
   * Example:
   * <pre>
   * TimeSeries timeSeries = TimeSeriesOp.indicator(input, Sign::isPositive);
   * Tensor result = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
   * </pre>
   * 
   * @param timeSeries with scalar value of either 0 or 1
   * @param predicate
   * @return */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> TimeSeries indicator(TimeSeries timeSeries, Predicate<T> predicate) {
    return TimeSeries.of(timeSeries.stream() //
        .map(entry -> new TsEntry(entry.key(), Boole.of(predicate.test((T) entry.value())))), //
        ResamplingMethods.HOLD_VALUE_FROM_LEFT_SPARSE);
  }

  /** @param timeSeries
   * @return
   * @throws Exception if time series is empty */
  public static Tensor firstValue(TimeSeries timeSeries) {
    return timeSeries.evaluate(timeSeries.domain().min());
  }

  /** @param timeSeries
   * @return
   * @throws Exception if time series is empty */
  public static Tensor lastValue(TimeSeries timeSeries) {
    return timeSeries.evaluate(timeSeries.domain().max());
  }
}
