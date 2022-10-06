// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.red.Inner;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Clip;

public enum TimeSeriesOp {
  ;
  private static final BinaryOperator<TimeSeries> PLUS = TimeSeriesBinaryOperator.of( //
      Tensor::add, ResamplingMethods.LINEAR_INTERPOLATION);
  private static final BinaryOperator<TimeSeries> MINUS = TimeSeriesBinaryOperator.of( //
      Tensor::subtract, ResamplingMethods.LINEAR_INTERPOLATION);
  private static final BinaryOperator<TimeSeries> TIMES = TimeSeriesBinaryOperator.of( //
      Inner.with((s, t) -> t.multiply(s)), ResamplingMethods.LINEAR_INTERPOLATION);

  /** implementation is consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the sum of the two given time series */
  public static TimeSeries add(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return PLUS.apply(timeSeries1, timeSeries2);
  }

  /** implementation is consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the timeSeries1 minus timeSeries2 */
  public static TimeSeries subtract(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return MINUS.apply(timeSeries1, timeSeries2);
  }

  /** implementation is consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the (approximate) pointwise product of the two given time series */
  public static TimeSeries times(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return TIMES.apply(timeSeries1, timeSeries2);
  }

  public static Optional<Tensor> reduce(TimeSeries timeSeries, BinaryOperator<Tensor> binaryOperator) {
    return timeSeries.stream() //
        .map(TsEntry::value) //
        .reduce(binaryOperator);
  }

  public static Optional<Tensor> max(TimeSeries timeSeries) {
    return reduce(timeSeries, Max::of);
  }

  public static Optional<Tensor> min(TimeSeries timeSeries) {
    return reduce(timeSeries, Max::of);
  }

  public static TimeSeries cut(TimeSeries timeSeries, Clip clip) {
    TimeSeries result = timeSeries.block(clip, true).copy();
    Scalar lo = clip.min();
    Scalar hi = clip.max();
    if (!result.containsKey(lo))
      timeSeries.insert(lo, timeSeries.evaluate(lo));
    if (!result.containsKey(hi))
      timeSeries.insert(hi, timeSeries.evaluate(hi));
    return timeSeries;
  }

  public static TimeSeries indicator(NavigableSet<Scalar> navigableSet) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return TimeSeries.of(navigableSet.stream() //
        .map(key -> new TsEntry(key, RealScalar.of(atomicInteger.getAndIncrement()))), //
        ResamplingMethods.HOLD_LO);
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
        ResamplingMethods.HOLD_LO_SPARSE);
  }

  /** @param timeSeries
   * @param suo
   * @return */
  public static TimeSeries mapValues(TimeSeries timeSeries, ScalarUnaryOperator suo) {
    return mapValues(timeSeries, suo, timeSeries.resamplingMethod());
  }

  /** @param timeSeries
   * @param suo
   * @param resamplingMethod
   * @return */
  public static TimeSeries mapValues(TimeSeries timeSeries, ScalarUnaryOperator suo, ResamplingMethod resamplingMethod) {
    return TimeSeries.of(timeSeries.stream() //
        .map(entry -> new TsEntry(entry.key(), suo.apply(Scalar.class.cast(entry.value())))), //
        resamplingMethod);
  }

  public static TimeSeries multiplyValues(TimeSeries timeSeries, Scalar factor) {
    return mapValues(timeSeries, factor::multiply);
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
