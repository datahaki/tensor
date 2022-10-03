// code by jph
package ch.alpine.tensor.prc;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.itp.LinearInterpolation;
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

  public static Tensor integrate(TimeSeries timeSeries) {
    return integrate(timeSeries, timeSeries.support());
  }

  /** Remark: implementation uses Euler-method for integration
   * which is exact for {@link ResamplingMethods#INTERPOLATION_1},
   * {@link ResamplingMethods#HOLD_LO}, etc.
   * 
   * @param timeSeries
   * @param clip must be subset of support of given time series
   * @return integral value
   * @throws Exception if clip is not a subset of support of given time series */
  public static Tensor integrate(TimeSeries timeSeries, Clip clip) {
    if (clip.equals(Clips.intersection(timeSeries.support(), clip))) {
      Scalar prev = clip.min();
      Tensor sum = timeSeries.eval(prev).multiply(clip.width().zero());
      for (Scalar next : timeSeries.keySet(clip)) {
        Clip interval = Clips.interval(prev, next);
        Scalar x = LinearInterpolation.of(interval).apply(RationalScalar.HALF);
        sum = sum.add(timeSeries.eval(x).multiply(interval.width()));
        prev = next;
      }
      Clip interval = Clips.interval(prev, clip.max());
      Scalar x = LinearInterpolation.of(interval).apply(RationalScalar.HALF);
      sum = sum.add(timeSeries.eval(x).multiply(interval.width()));
      return sum;
    }
    throw new Throw(clip);
  }
}
