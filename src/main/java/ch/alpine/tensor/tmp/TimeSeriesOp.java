// code by jph
package ch.alpine.tensor.tmp;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public enum TimeSeriesOp {
  ;
  /** consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the sum of the two given time series */
  public static TimeSeries add(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return fuse(timeSeries1, timeSeries2, Tensor::add);
  }

  /** consistent with Mathematica
   * 
   * @param timeSeries1
   * @param timeSeries2
   * @return time series that is the (approximate) pointwise product of the two given time series */
  public static TimeSeries times(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return fuse(timeSeries1, timeSeries2, Times.operator());
  }

  public static TimeSeries fuse( //
      TimeSeries timeSeries1, //
      TimeSeries timeSeries2, //
      BinaryOperator<Tensor> binaryOperator) {
    if (timeSeries1.resamplingMethod().equals(timeSeries2.resamplingMethod())) {
      Clip clip = Clips.intersection(timeSeries1.support(), timeSeries2.support());
      // TODO TENSOR SYNC with mathematica: what happens if clip is empty
      TimeSeries timeSeries = TimeSeries.empty(timeSeries1.resamplingMethod());
      Set<Scalar> set = new HashSet<>();
      set.addAll(timeSeries1.keySet(clip));
      set.addAll(timeSeries2.keySet(clip));
      for (Scalar key : set)
        timeSeries.insert(key, binaryOperator.apply(timeSeries1.eval(key), timeSeries2.eval(key)));
      return timeSeries;
    }
    throw new Throw(timeSeries1, timeSeries2);
  }

  public static void extend(TimeSeries timeSeries, Scalar key) {
    Clip clip = timeSeries.support();
    if (clip.isOutside(key))
      timeSeries.insert(key, timeSeries.eval(clip.max()));
  }
}
