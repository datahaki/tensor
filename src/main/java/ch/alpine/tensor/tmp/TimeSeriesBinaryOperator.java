// code by jph
package ch.alpine.tensor.tmp;

import java.io.Serializable;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** binary operator allows that fuses two time series into a new one. This can
 * be for instance, the pointwise sum, or product.
 * 
 * For two input time series with overlapping domains, the implementation is
 * consistent with Mathematica.
 * 
 * When either input time series is empty, or their {@link TimeSeries#domain()}s
 * have no point in common, then the tensor library returns an empty time series.
 * 
 * <p>Remark:
 * Mathematica does not permit the addition/multiplication of time series
 * with non-overlapping domains. */
public class TimeSeriesBinaryOperator implements BinaryOperator<TimeSeries>, Serializable {
  /** @param binaryOperator
   * @param resamplingMethod fallback may be null */
  public static BinaryOperator<TimeSeries> of( //
      BinaryOperator<Tensor> binaryOperator, //
      ResamplingMethod resamplingMethod) {
    return new TimeSeriesBinaryOperator( //
        Objects.requireNonNull(binaryOperator), //
        resamplingMethod);
  }

  // ---
  private final BinaryOperator<Tensor> binaryOperator;
  private final ResamplingMethod resamplingMethod;

  /** @param binaryOperator
   * @param resamplingMethod fallback may be null */
  private TimeSeriesBinaryOperator(BinaryOperator<Tensor> binaryOperator, ResamplingMethod resamplingMethod) {
    this.binaryOperator = Objects.requireNonNull(binaryOperator);
    this.resamplingMethod = resamplingMethod;
  }

  @Override
  public TimeSeries apply(TimeSeries timeSeries1, TimeSeries timeSeries2) {
    ResamplingMethod resamplingMethod = timeSeries1.resamplingMethod().equals(timeSeries2.resamplingMethod()) //
        ? timeSeries1.resamplingMethod()
        : Objects.requireNonNull(this.resamplingMethod);
    if (!timeSeries1.isEmpty() && !timeSeries2.isEmpty()) {
      Clip clip1 = timeSeries1.domain();
      Clip clip2 = timeSeries2.domain();
      if (Clips.nonEmptyIntersection(clip1, clip2)) {
        Clip clip = Clips.intersection(clip1, clip2);
        NavigableSet<Scalar> navigableSet = new TreeSet<>();
        navigableSet.addAll(timeSeries1.keySet(clip, true));
        navigableSet.addAll(timeSeries2.keySet(clip, true));
        return TimeSeries.of(navigableSet.stream() //
            .map(key -> new TsEntry(key, binaryOperator.apply( //
                timeSeries1.evaluate(key), //
                timeSeries2.evaluate(key)))), //
            resamplingMethod);
      }
    }
    return TimeSeries.empty(resamplingMethod);
  }
}
