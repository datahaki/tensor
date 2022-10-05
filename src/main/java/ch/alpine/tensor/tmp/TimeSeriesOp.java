// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Inner;

public enum TimeSeriesOp {
  ;
  private static final BinaryOperator<TimeSeries> PLUS = TimeSeriesBinaryOperator.of(Tensor::add, null);
  private static final BinaryOperator<TimeSeries> TIMES = TimeSeriesBinaryOperator.of(Inner.with((s, t) -> t.multiply(s)), null);

  /** consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the sum of the two given time series */
  public static TimeSeries add(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return PLUS.apply(timeSeries1, timeSeries2);
  }

  /** consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the (approximate) pointwise product of the two given time series */
  public static TimeSeries times(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return TIMES.apply(timeSeries1, timeSeries2);
  }

  public static Optional<Tensor> reduce(TimeSeries timeSeries, BinaryOperator<Tensor> binaryOperator) {
    return timeSeries.stream().map(TsEntry::value).reduce(binaryOperator);
  }

  public static TimeSeries indicator(NavigableSet<Scalar> navigableSet) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return TimeSeries.of(navigableSet.stream() //
        .map(key -> new TsEntry(key, RealScalar.of(atomicInteger.getAndIncrement()))), //
        ResamplingMethods.HOLD_LO);
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
