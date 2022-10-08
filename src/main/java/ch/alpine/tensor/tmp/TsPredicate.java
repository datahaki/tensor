// code by jph
package ch.alpine.tensor.tmp;

public enum TsPredicate {
  ;
  public static boolean equals(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    return timeSeries1.resamplingMethod().equals(timeSeries2.resamplingMethod()) //
        && timeSeries1.size() == timeSeries2.size() //
        && timeSeries1.stream().allMatch(entry -> //
        timeSeries2.containsKey(entry.key()) //
            && entry.value().equals(timeSeries2.evaluate(entry.key())));
  }

  public static boolean isUnmodifiable(TimeSeries timeSeries) {
    return timeSeries.unmodifiable() == timeSeries; // equality by reference intended
  }
}
