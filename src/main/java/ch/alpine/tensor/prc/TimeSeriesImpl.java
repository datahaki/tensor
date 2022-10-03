// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ class TimeSeriesImpl implements TimeSeries, Serializable {
  private final NavigableMap<Scalar, Tensor> navigableMap;
  private final ResamplingMethod resamplingMethod;

  public TimeSeriesImpl(NavigableMap<Scalar, Tensor> navigableMap, ResamplingMethod resamplingMethod) {
    this.navigableMap = navigableMap;
    this.resamplingMethod = resamplingMethod;
  }

  @Override // from TimeSeries
  public ResamplingMethod resamplingMethod() {
    return resamplingMethod;
  }

  @Override // from TimeSeries
  public void insert(Scalar key, Tensor value) {
    resamplingMethod.insert(navigableMap, key, value);
  }

  @Override // from TimeSeries
  public Tensor eval(Scalar x) {
    return resamplingMethod.evaluate(navigableMap, x);
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

  @Override // from TimeSeries
  public TimeSeries copy() {
    return new TimeSeriesImpl(navigableMap.entrySet().stream() //
        .collect(Collectors.toMap( //
            Entry::getKey, //
            entry -> entry.getValue().copy(), //
            MergeIllegal.operator(), //
            TreeMap::new)),
        resamplingMethod);
  }

  @Override // from TimeSeries
  public TimeSeries unmodifiable() {
    return new TimeSeriesImpl(Collections.unmodifiableNavigableMap(navigableMap), resamplingMethod);
  }

  @Override // from TimeSeries
  public TimeSeries extract(Clip clip) {
    return block(clip).copy();
  }

  @Override // from TimeSeries
  public TimeSeries block(Clip clip) {
    return new TimeSeriesImpl(navigableMap.subMap(clip.min(), true, clip.max(), true), resamplingMethod);
  }

  @Override // from TimeSeries
  public Tensor path() {
    return Tensor.of(navigableMap.entrySet().stream() //
        .map(entry -> Tensors.of(entry.getKey(), entry.getValue())));
  }

  @Override // from TimeSeries
  public Tensor times() {
    return Tensor.of(navigableMap.keySet().stream());
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("TimeSeries", size(), resamplingMethod());
  }
}
