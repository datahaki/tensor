// code by jph
package ch.alpine.tensor.prc;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.sca.Clip;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeries.html">TimeSeries</a> */
public interface TimeSeries {
  /** @return empty time series with linear interpolation as resampling method */
  static TimeSeries empty() {
    return empty(ResamplingMethods.INTERPOLATION_1);
  }

  /** @param resamplingMethod
   * @return empty time series with given resampling method */
  static TimeSeries empty(ResamplingMethod resamplingMethod) {
    return new TimeSeriesImpl(new TreeMap<>(), Objects.requireNonNull(resamplingMethod));
  }

  static TimeSeries of(Tensor path) {
    return of(path.stream());
  }

  /** @param stream of tensors, where each is of the form {key, value}
   * @return
   * @throws Exception if any tensor in the stream does not have length 2 */
  static TimeSeries of(Stream<Tensor> stream) {
    return of(stream, ResamplingMethods.INTERPOLATION_1);
  }

  /** @param stream of tensors, where each is of the form {key, value}
   * @param resamplingMethod
   * @return
   * @throws Exception if any tensor in the stream does not have length 2 */
  static TimeSeries of(Stream<Tensor> stream, ResamplingMethod resamplingMethod) {
    return new TimeSeriesImpl(stream.map(tensor -> {
      Integers.requireEquals(tensor.length(), 2);
      return tensor;
    }).collect(Collectors.toMap( //
        tensor -> tensor.Get(0), //
        tensor -> tensor.get(1), //
        MergeIllegal.operator(), //
        TreeMap::new)), Objects.requireNonNull(resamplingMethod));
  }

  // ---
  /** @return method for computation of values inside {@link #support()} */
  ResamplingMethod resamplingMethod();

  /** associates a copy of given value with key
   * 
   * @param key
   * @param value
   * @throws Exception if either parameter is null */
  void insert(Scalar key, Tensor value);

  /** @param x inside {@link #support()}
   * @return
   * @throws Exception if time series is empty */
  Tensor eval(Scalar x);

  Tensor extend(Scalar key);

  /** @return interval [firstKey(), lastKey()]
   * @throws Exception if time series is empty */
  Clip support();

  /** @return number of (key, value)-pairs */
  int size();

  /** @return whether time series is without (key, value)-pairs */
  boolean isEmpty();

  /** @return duplicate of this time series */
  TimeSeries copy();

  /** @return unmodifiable view of this time series */
  TimeSeries unmodifiable();

  /** @param clip
   * @return copy of submap */
  TimeSeries extract(Clip clip);

  /** @param clip
   * @return view on submap */
  TimeSeries block(Clip clip);

  /** @param clip
   * @return */
  NavigableSet<Scalar> keySet(Clip clip);

  /** Mathematica convention
   * 
   * @return */
  Tensor times();

  /** Mathematica convention
   * 
   * @return */
  Tensor path();
}
