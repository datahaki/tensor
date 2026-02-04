// code by jph
package ch.alpine.tensor.tmp;

import java.util.Optional;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.red.Entrywise;

/** reduction of all values from a time series to a single value */
// TODO TENSOR API TsXY possibly rename
public enum TsReduce {
  ;
  /** @param timeSeries
   * @param binaryOperator
   * @return reduction of all values in given time series with respect to given operator,
   * or empty, if given time series is empty */
  public static Optional<Tensor> reduce(TimeSeries timeSeries, TensorBinaryOperator binaryOperator) {
    return timeSeries.stream() //
        .map(TsEntry::value) //
        .reduce(binaryOperator);
  }

  /** @param timeSeries
   * @return entrywise max value of all values in given time series */
  public static Optional<Tensor> max(TimeSeries timeSeries) {
    return reduce(timeSeries, Entrywise.max());
  }

  /** @param timeSeries
   * @return entrywise min value of all values in given time series */
  public static Optional<Tensor> min(TimeSeries timeSeries) {
    return reduce(timeSeries, Entrywise.min());
  }

  /** @param timeSeries
   * @return value of first entry in given time series
   * @throws Exception if time series is empty */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> Optional<T> firstValue(TimeSeries timeSeries) {
    return timeSeries.isEmpty() //
        ? Optional.empty()
        : Optional.of((T) timeSeries.evaluate(timeSeries.domain().min()));
  }

  /** @param timeSeries
   * @return value of last entry in given time series
   * @throws Exception if time series is empty */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> Optional<T> lastValue(TimeSeries timeSeries) {
    return timeSeries.isEmpty() //
        ? Optional.empty()
        : Optional.of((T) timeSeries.evaluate(timeSeries.domain().max()));
  }
}
