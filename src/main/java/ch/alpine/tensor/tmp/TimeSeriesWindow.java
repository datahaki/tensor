// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;

/** Careful:
 * implementation is not consistent with Mathematica::TimeSeriesWindow
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeriesWindow.html">TimeSeriesWindow</a> */
public enum TimeSeriesWindow {
  ;
  /** duplicates an excerpt from a given time series
   * and ensures that the clip endpoints are contained in the key set
   * 
   * @param timeSeries
   * @param clip subset of timeSeries.domain()
   * @return
   * @throws Exception if domain of given time series does not cover clip */
  public static TimeSeries of(TimeSeries timeSeries, Clip clip) {
    TimeSeries result = timeSeries.block(clip, true).copy();
    Scalar lo = clip.min();
    Scalar hi = clip.max();
    if (!result.containsKey(lo))
      result.insert(lo, timeSeries.evaluate(lo));
    if (!result.containsKey(hi))
      result.insert(hi, timeSeries.evaluate(hi));
    return result;
  }
}
