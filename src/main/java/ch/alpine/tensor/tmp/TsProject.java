package ch.alpine.tensor.tmp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

public enum TsProject {
  ;
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
}
