// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Integrate.html">Integrate</a> */
public enum TimeSeriesIntegrate {
  ;
  /** Remark: the returned time series evaluates at the highest key
   * the same as the methods {@link #of(TimeSeries, Clip)} when the full
   * support is provided as clip.
   * 
   * @param timeSeries
   * @return new instance of time series with identical keys of given timeSeries
   * and values that are the accumulated integration */
  public static TimeSeries of(TimeSeries timeSeries) {
    TimeSeries result = TimeSeries.empty(ResamplingMethods.LINEAR_INTERPOLATION);
    Clip clip = timeSeries.support();
    Scalar prev = clip.min();
    Tensor sum = timeSeries.eval(prev).multiply(clip.width().zero());
    result.insert(prev, sum);
    for (Scalar next : timeSeries.keySet(clip, true)) {
      Clip interval = Clips.interval(prev, next);
      Scalar x = LinearInterpolation.of(interval).apply(RationalScalar.HALF);
      sum = sum.add(timeSeries.eval(x).multiply(interval.width()));
      result.insert(next, sum);
      prev = next;
    }
    Clip interval = Clips.interval(prev, clip.max());
    Scalar x = LinearInterpolation.of(interval).apply(RationalScalar.HALF);
    sum = sum.add(timeSeries.eval(x).multiply(interval.width()));
    result.insert(prev, sum);
    return result;
  }

  /** Remark: implementation uses Euler-method for integration
   * which is exact for the resampling methods:
   * {@link ResamplingMethods#LINEAR_INTERPOLATION},
   * {@link ResamplingMethods#HOLD_LO}, etc.
   * 
   * @param timeSeries
   * @param clip must be subset of support of given time series
   * @return integral value
   * @throws Exception if clip is not a subset of support of given time series */
  public static Tensor of(TimeSeries timeSeries, Clip clip) {
    if (clip.equals(Clips.intersection(timeSeries.support(), clip))) {
      Scalar prev = clip.min();
      Tensor sum = timeSeries.eval(prev).multiply(clip.width().zero());
      for (Scalar next : timeSeries.keySet(clip, true)) {
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
