// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;

public enum TimeSeriesOp {
  ;
  private static final BinaryOperator<TimeSeries> PLUS = TimeSeriesBinaryOperator.of(Tensor::add, null);
  private static final BinaryOperator<TimeSeries> TIMES = TimeSeriesBinaryOperator.of(Times.operator(), null);

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

  public static void extend(TimeSeries timeSeries, Scalar key) {
    Clip clip = timeSeries.domain();
    if (Scalars.lessThan(key, clip.min()))
      timeSeries.insert(key, timeSeries.evaluate(clip.min()));
    if (Scalars.lessThan(clip.max(), key))
      timeSeries.insert(key, timeSeries.evaluate(clip.max()));
  }

  public static void extend(TimeSeries timeSeries, Clip domain) {
    Clip clip = timeSeries.domain();
    if (Scalars.lessThan(domain.min(), clip.min()))
      timeSeries.insert(domain.min(), timeSeries.evaluate(clip.min()));
    if (Scalars.lessThan(clip.max(), domain.max()))
      timeSeries.insert(domain.max(), timeSeries.evaluate(clip.max()));
  }

  public static TimeSeries indicator(NavigableSet<Scalar> navigableSet) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return TimeSeries.of(navigableSet.stream() //
        .map(key -> new TsEntry(key, RealScalar.of(atomicInteger.getAndIncrement()))), //
        ResamplingMethods.HOLD_LO);
  }
}
