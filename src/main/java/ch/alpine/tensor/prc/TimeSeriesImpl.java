// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesImpl implements TimeSeries, Serializable {
  private final NavigableMap<Scalar, Tensor> navigableMap;

  public TimeSeriesImpl(NavigableMap<Scalar, Tensor> navigableMap) {
    this.navigableMap = navigableMap;
  }

  @Override // from TimeSeries
  public Tensor insert(Scalar key, Tensor value) {
    return navigableMap.put(key, Objects.requireNonNull(value));
  }

  @Override // from TimeSeries
  public Tensor extend(Scalar key) {
    Entry<Scalar, Tensor> entry = navigableMap.lastEntry();
    if (Scalars.lessEquals(entry.getKey(), key)) {
      if (!entry.getKey().equals(key))
        navigableMap.put(key, entry.getValue());
      return entry.getValue();
    }
    throw new Throw(key);
  }

  @Override // from TimeSeries
  public Tensor step(Scalar x) {
    if (Scalars.lessEquals(x, navigableMap.lastKey()))
      return navigableMap.floorEntry(x).getValue().copy();
    throw new Throw(x);
  }

  @Override // from TimeSeries
  public Tensor lerp(Scalar x) {
    Entry<Scalar, Tensor> lo = navigableMap.floorEntry(x);
    Entry<Scalar, Tensor> hi = navigableMap.ceilingEntry(x);
    return LinearInterpolation.of(Tensors.of( //
        lo.getValue(), //
        hi.getValue())).at(Clips
            .interval( //
                lo.getKey(), //
                hi.getKey())
            .rescale(x));
  }

  @Override
  public NavigableSet<Scalar> keySet(Clip clip) {
    return navigableMap.subMap(clip.min(), true, clip.max(), true).navigableKeySet();
  }

  @Override // from TimeSeries
  public Clip support() {
    return Clips.keycover(navigableMap);
  }

  @Override // from TimeSeries
  public int size() {
    return navigableMap.size();
  }

  @Override // from TimeSeries
  public boolean isEmpty() {
    return navigableMap.isEmpty();
  }

  @Override
  public TimeSeries copy() {
    return new TimeSeriesImpl(navigableMap.entrySet().stream() //
        .collect(Collectors.toMap( //
            Entry::getKey, //
            entry -> entry.getValue().copy(), //
            MergeIllegal.operator(), //
            TreeMap::new)));
  }

  @Override
  public TimeSeries unmodifiable() {
    return new TimeSeriesImpl(Collections.unmodifiableNavigableMap(navigableMap));
  }

  @Override
  public TimeSeries extract(Clip clip) {
    return block(clip).copy();
  }

  @Override
  public TimeSeries block(Clip clip) {
    return new TimeSeriesImpl(navigableMap.subMap(clip.min(), true, clip.max(), true));
  }

  @Override // from TimeSeries
  public Tensor path() {
    return Tensor.of(navigableMap.entrySet().stream() //
        .map(entry -> Tensors.of(entry.getKey(), entry.getValue())));
  }

  @Override
  public Tensor times() {
    return Tensor.of(navigableMap.keySet().stream());
  }

  @Override
  public int hashCode() {
    return navigableMap.hashCode();
  }
}
