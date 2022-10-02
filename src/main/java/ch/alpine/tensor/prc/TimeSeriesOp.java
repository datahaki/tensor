// code by jph
package ch.alpine.tensor.prc;

import java.util.HashSet;
import java.util.Set;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public enum TimeSeriesOp {
  ;
  public static TimeSeries add(TimeSeries ts1, TimeSeries ts2) {
    Clip clip = Clips.intersection(ts1.support(), ts2.support());
    TimeSeries timeSeries = TimeSeries.empty();
    Set<Scalar> set = new HashSet<>();
    set.addAll(ts1.keySet().subSet(clip.min(), true, clip.max(), true));
    set.addAll(ts2.keySet().subSet(clip.min(), true, clip.max(), true));
    for (Scalar key : set)
      timeSeries.insert(key, ts1.lerp(key).add(ts2.lerp(key)));
    return timeSeries;
  }
}
