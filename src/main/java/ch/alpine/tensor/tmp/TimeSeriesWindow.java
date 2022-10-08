// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;

/** implementation is not consistent with Mathematica
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeriesWindow.html">TimeSeriesWindow</a> */
public enum TimeSeriesWindow {
  ;
  /** @param timeSeries
   * @param clip
   * @return */
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
