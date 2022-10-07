// code by jph
package ch.alpine.tensor.tmp;

import java.util.Optional;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;

public enum TsReduce {
  ;
  public static Optional<Tensor> reduce(TimeSeries timeSeries, BinaryOperator<Tensor> binaryOperator) {
    return timeSeries.stream() //
        .map(TsEntry::value) //
        .reduce(binaryOperator);
  }

  public static Optional<Tensor> max(TimeSeries timeSeries) {
    return reduce(timeSeries, Max::of);
  }

  public static Optional<Tensor> min(TimeSeries timeSeries) {
    return reduce(timeSeries, Min::of);
  }
}
