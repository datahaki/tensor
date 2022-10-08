// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clip;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeriesRescale.html">TimeSeriesRescale</a> */
public enum TimeSeriesRescale {
  ;
  /** @param timeSeries
   * @param clip
   * @return new time series with domain as clip */
  public static TimeSeries of(TimeSeries timeSeries, Clip clip) {
    Clip domain = timeSeries.domain();
    ScalarUnaryOperator suo = LinearInterpolation.of(clip);
    return TimeSeries.of(timeSeries.stream() //
        .map(entry -> new TsEntry( //
            suo.apply(domain.rescale(entry.key())), //
            entry.value().copy())),
        timeSeries.resamplingMethod());
  }
}
