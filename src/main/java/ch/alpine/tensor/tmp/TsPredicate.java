// code by jph
package ch.alpine.tensor.tmp;

public enum TsPredicate {
  ;
  /** @param timeSeries1
   * @param timeSeries2
   * @return whether the two given time series are considered equal, i.e. if they
   * have the same resampling method as well as identical (key, value)-pairs */
  public static boolean equals(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return timeSeries1.resamplingMethod().equals(timeSeries2.resamplingMethod()) //
        && timeSeries1.size() == timeSeries2.size() //
        && timeSeries1.stream().allMatch(entry -> //
        timeSeries2.containsKey(entry.key()) //
            && entry.value().equals(timeSeries2.evaluate(entry.key())));
  }

  /** @param timeSeries
   * @return whether given time series is unmodifiable */
  public static boolean isUnmodifiable(TimeSeries timeSeries) {
    return timeSeries.unmodifiable() == timeSeries; // equality by reference intended
  }
}
