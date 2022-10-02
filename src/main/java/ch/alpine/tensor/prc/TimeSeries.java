// code by jph
package ch.alpine.tensor.prc;

import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.sca.Clip;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeries.html">TimeSeries</a> */
public interface TimeSeries {
  static TimeSeries of(Tensor path) {
    return of(path.stream());
  }

  static TimeSeries of(Stream<Tensor> stream) {
    return new TimeSeriesImpl(stream.collect(Collectors.toMap(tensor -> tensor.Get(0), tensor -> tensor.get(1), MergeIllegal.operator(), TreeMap::new)));
  }

  static TimeSeries empty() {
    return new TimeSeriesImpl(new TreeMap<>());
  }

  Tensor insert(Scalar key, Tensor asd);

  Tensor extend(Scalar key);

  /** @param x inside support
   * @return
   * @throws Exception if time series is empty */
  Tensor step(Scalar x);

  /** @param x inside support
   * @return
   * @throws Exception if time series is empty */
  Tensor lerp(Scalar x);

  /** @return
   * @throws Exception if time series is empty */
  Clip support();

  int size();

  boolean isEmpty();

  TimeSeries copy();

  TimeSeries unmodifiable();

  TimeSeries extract(Clip clip);

  TimeSeries block(Clip clip);

  NavigableSet<Scalar> keySet();

  /** Mathematica convention
   * 
   * @return */
  Tensor times();

  /** Mathematica convention
   * 
   * @return */
  Tensor path();
}
