// code by jph
package ch.alpine.tensor.tmp;

import java.util.Iterator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Clip;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MinimumTimeIncrement.html">MinimumTimeIncrement</a> */
public enum MinimumTimeIncrement {
  ;
  /** @param timeSeries non-empty
   * @return */
  public static Scalar of(TimeSeries timeSeries) {
    Clip clip = timeSeries.domain();
    Scalar min = clip.width();
    Iterator<Scalar> iterator = timeSeries.keySet(clip, true).iterator();
    Scalar prev = iterator.next();
    while (iterator.hasNext()) {
      Scalar next = iterator.next();
      min = Min.of(min, next.subtract(prev));
      prev = next;
    }
    return min;
  }
}
