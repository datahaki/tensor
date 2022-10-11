// code by jph
package ch.alpine.tensor.tmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

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

  /** applies function to all values and then tests the resulting scalar
   * with given predicate. the segments where the tests consecutively evaluate
   * to true are collected in a clip. All such clips combined are returned
   * in order.
   * 
   * clip.min tests to true
   * clip.max tests to false unless clip.max == clip.min
   * 
   * @param timeSeries
   * @param predicate
   * @return */
  public static List<Clip> regions(TimeSeries timeSeries, Predicate<Scalar> predicate) {
    return regions(timeSeries, Scalar.class::cast, predicate);
  }

  /** applies function to all values and then tests the resulting scalar
   * with given predicate. the segments where the tests consecutively evaluate
   * to true are collected in a clip. All such clips combined are returned
   * in order.
   * 
   * clip.min tests to true
   * clip.max tests to false unless clip.max == clip.min
   * 
   * @param timeSeries
   * @param function
   * @param predicate
   * @return */
  public static List<Clip> regions(TimeSeries timeSeries, TensorScalarFunction function, Predicate<Scalar> predicate) {
    Clip domain = timeSeries.domain();
    List<Clip> list = new ArrayList<>();
    AtomicReference<Scalar> min = new AtomicReference<>();
    timeSeries.stream().forEach(entry -> {
      if (predicate.test(function.apply(entry.value()))) {
        if (Objects.isNull(min.get()))
          min.set(entry.key());
      } else //
      if (Objects.nonNull(min.get())) {
        list.add(Clips.interval(min.get(), entry.key()));
        min.set(null);
      }
    });
    if (Objects.nonNull(min.get()))
      list.add(Clips.interval(min.get(), domain.max()));
    return list;
  }
}
