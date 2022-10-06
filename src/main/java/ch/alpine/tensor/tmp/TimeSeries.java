// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.sca.Clip;

/** A time series hosts a discrete set of (key, value)-pairs, i.e.
 * (Scalar, Tensor)-pairs, and uses a {@link ResamplingMethod} to
 * map {@link Scalar} within the {@link #domain()} to a
 * {@link Tensor}.
 * 
 * <p>In Mathematica, the default resampling method is linear
 * interpolation, which is available here as
 * {@link ResamplingMethods#LINEAR_INTERPOLATION}.
 * 
 * <p>All values in the time series must have the same tensor structure.
 * {@link #insert(Scalar, Tensor)} throws an exception if the value
 * to be inserted has a different structure.
 * 
 * <p>Some implementations of the interface {@link TimeSeries}
 * are unmodifiable. In that case, {@link #insert(Scalar, Tensor)}
 * throws an Exception, and the values provided to the "outside"
 * are unmodifiable tensors, or copied of the values stored in the
 * time series.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TimeSeries.html">TimeSeries</a> */
public interface TimeSeries {
  /** @param resamplingMethod non-null
   * @return empty time series with given resampling method */
  static TimeSeries empty(ResamplingMethod resamplingMethod) {
    return new TimeSeriesImpl(new TreeMap<>(), Objects.requireNonNull(resamplingMethod));
  }

  /** @param stream
   * @param resamplingMethod
   * @return
   * @throws Exception in case of duplicate keys */
  static TimeSeries of(Stream<TsEntry> stream, ResamplingMethod resamplingMethod) {
    return new TimeSeriesImpl(resamplingMethod.pack(stream.collect(Collectors.toMap( //
        TsEntry::key, //
        TsEntry::value, //
        MergeIllegal.operator(), //
        TreeMap::new))), //
        resamplingMethod);
  }

  /** @param path with entries of the form {key, value}
   * @param resamplingMethod
   * @return
   * @throws Exception in case of duplicate keys */
  static TimeSeries path(Tensor path, ResamplingMethod resamplingMethod) {
    return path(path.stream(), resamplingMethod);
  }

  /** @param stream of tensors, where each is of the form {key, value}
   * @param resamplingMethod
   * @return
   * @throws Exception if any tensor in the stream does not have length 2
   * @throws Exception in case of duplicate keys */
  static TimeSeries path(Stream<Tensor> stream, ResamplingMethod resamplingMethod) {
    return new TimeSeriesImpl(resamplingMethod.pack(stream
        .peek(tensor -> Integers.requireEquals(tensor.length(), 2)) //
        .collect(Collectors.toMap( //
            tensor -> tensor.Get(0), //
            tensor -> tensor.get(1), //
            MergeIllegal.operator(), //
            TreeMap::new))), resamplingMethod);
  }

  /** @param stream
   * @param resamplingMethod
   * @return */
  static TimeSeries table(Stream<Tensor> stream, ResamplingMethod resamplingMethod) {
    return new TimeSeriesImpl(resamplingMethod.pack(stream.collect(Collectors.toMap( //
        tensor -> tensor.Get(0), //
        tensor -> tensor.extract(1, tensor.length()), //
        MergeIllegal.operator(), //
        TreeMap::new))), resamplingMethod);
  }

  /** @param navigableSet
   * @param function to which the time series only supplies values from given navigable set
   * @param resamplingMethod
   * @return view on an external data-set for the purpose of interpolation, read only */
  static TimeSeries wrap(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, ResamplingMethod resamplingMethod) {
    return new TimeSeriesWrap( //
        Objects.requireNonNull(navigableSet), //
        Objects.requireNonNull(function), //
        Objects.requireNonNull(resamplingMethod));
  }

  // ---
  /** @return method for computation of values inside {@link #domain()} */
  ResamplingMethod resamplingMethod();

  /** @return unmodifiable view of this time series */
  TimeSeries unmodifiable();

  /** @return duplicate of this time series */
  TimeSeries copy();

  /** associates a copy of given value with key
   * 
   * @param key
   * @param value
   * @throws Exception if the tensor structure of given value are not
   * the same as the structure of the other values in the map
   * @throws Exception if either parameter is null */
  void insert(Scalar key, Tensor value);

  /** @param x inside {@link #domain()}
   * @return
   * @throws Exception if x is not inside {@link #domain()}, for instance
   * when this time series is empty */
  Tensor evaluate(Scalar x);

  /** the domain is always the min/max interval of all the keys
   * that were inserted via {@link #insert(Scalar, Tensor)}
   * 
   * @return interval [firstKey(), lastKey()]
   * @throws Exception if time series is empty */
  Clip domain();

  /** @param key
   * @return whether a value is associated to the exact key */
  boolean containsKey(Scalar key);

  /** @return number of (key, value)-pairs */
  int size();

  /** @return whether time series is without (key, value)-pairs */
  boolean isEmpty();

  /** @param clip
   * @param maxInclusive whether a key of value clip.max() should be included in the set
   * @return */
  NavigableSet<Scalar> keySet(Clip clip, boolean maxInclusive);

  /** @param clip
   * @param maxInclusive
   * @return view on part of this time series in the given clip range */
  TimeSeries block(Clip clip, boolean maxInclusive);

  /** Careful: The values of this time series are provided by reference
   * in {@link TsEntry#value()}. Modifications to a value alters this time series.
   * This design choice is in the same spirit as {@link NavigableMap} and
   * {@link Tensor#stream()}.
   * 
   * Hint: in order to obtain a stream over a subset of this time
   * series prepend {@link #block(Clip, boolean)} before invoking
   * {@link #stream()}, i.e.
   * <pre>
   * timeSeries.block(clip, true). stream()
   * </pre>
   * 
   * @return stream over all (key, value)-pairs in this time series */
  Stream<TsEntry> stream();

  /** corresponds to TimeSeries[...]["Path"] in Mathematica
   * 
   * @return tensor with dimensions size() x 2 */
  Tensor path();
}
