// code by jph
package ch.alpine.tensor.tmp;

import ch.alpine.tensor.io.MathematicaFormat;

/* package */ abstract class AbstractTimeSeries implements TimeSeries {
  @Override
  public final int hashCode() {
    return stream() //
        .mapToInt(entry -> entry.key().hashCode() + entry.value().hashCode()) //
        .sum() + resamplingMethod().hashCode();
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof TimeSeries) {
      TimeSeries timeSeries = (TimeSeries) object;
      return resamplingMethod().equals(timeSeries.resamplingMethod()) //
          && size() == timeSeries.size() //
          && stream().allMatch(entry -> //
          timeSeries.containsKey(entry.key()) //
              && entry.value().equals(timeSeries.evaluate(entry.key())));
    }
    return false;
  }

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("TimeSeries", size(), resamplingMethod());
  }
}
