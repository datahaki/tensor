// code by jph
package ch.alpine.tensor.prc;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public enum TimeSeriesOp {
  ;
  /** @param timeSeries1
   * @param timeSeries2
   * @return time series that is the sum of the two given time series */
  public static TimeSeries add(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return fuse(timeSeries1, timeSeries2, Tensor::add);
  }

  /** @param timeSeries1
   * @param timeSeries2
   * @return time series that is the sum of the two given time series */
  public static TimeSeries times(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return fuse(timeSeries1, timeSeries2, Times.operator());
  }

  private static TimeSeries fuse( //
      TimeSeries timeSeries1, //
      TimeSeries timeSeries2, //
      BinaryOperator<Tensor> binaryOperator) {
    Clip clip = Clips.intersection(timeSeries1.support(), timeSeries2.support());
    TimeSeries timeSeries = TimeSeries.empty();
    Set<Scalar> set = new HashSet<>();
    set.addAll(timeSeries1.keySet(clip));
    set.addAll(timeSeries2.keySet(clip));
    for (Scalar key : set)
      timeSeries.insert(key, binaryOperator.apply(timeSeries1.eval(key), timeSeries2.eval(key)));
    return timeSeries;
  }

  public static Tensor integral(TimeSeries timeSeries) {
    return integral(timeSeries, timeSeries.support());
  }

  public static Tensor integral(TimeSeries timeSeries, Clip clip) {
    NavigableSet<Scalar> navigableSet = timeSeries.keySet(clip);
    // FIXME
    return null;
  }
}
