// code by jph
package ch.alpine.tensor.tmp;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ class TimeSeriesImpl implements TimeSeries, Serializable {
  /* package */ final NavigableMap<Scalar, Tensor> navigableMap;
  private final ResamplingMethod resamplingMethod;

  public TimeSeriesImpl(NavigableMap<Scalar, Tensor> navigableMap, ResamplingMethod resamplingMethod) {
    this.navigableMap = navigableMap;
    this.resamplingMethod = resamplingMethod;
  }

  @Override // from TimeSeries
  public final ResamplingMethod resamplingMethod() {
    return resamplingMethod;
  }

  @Override // from TimeSeries
  public TimeSeries unmodifiable() {
    return new UnmodifiableTimeSeries(Collections.unmodifiableNavigableMap(navigableMap), resamplingMethod);
  }

  @Override // from TimeSeries
  public final TimeSeries copy() {
    return new TimeSeriesImpl(navigableMap.entrySet().stream() //
        .collect(Collectors.toMap( //
            Entry::getKey, //
            entry -> entry.getValue().copy(), //
            MergeIllegal.operator(), //
            TreeMap::new)),
        resamplingMethod);
  }

  @Override // from TimeSeries
  public void insert(Scalar key, Tensor value) {
    Entry<Scalar, Tensor> entry = navigableMap.firstEntry();
    resamplingMethod.insert(navigableMap, key, Objects.isNull(entry) //
        ? value.copy()
        : StaticHelper.SECOND.apply(entry.getValue(), value));
  }

  @Override // from TimeSeries
  public Tensor eval(Scalar x) {
    return resamplingMethod.evaluate(navigableMap, x);
  }

  @Override // from TimeSeries
  public final Clip support() {
    return Clips.keycover(navigableMap);
  }

  @Override // from TimeSeries
  public final int size() {
    return navigableMap.size();
  }

  @Override // from TimeSeries
  public final boolean isEmpty() {
    return navigableMap.isEmpty();
  }

  @Override // from TimeSeries
  public NavigableSet<Scalar> keySet(Clip clip, boolean maxInclusive) {
    return navigableMap.subMap(clip.min(), true, clip.max(), maxInclusive).navigableKeySet();
  }

  @Override // from TimeSeries
  public Stream<TsEntry> stream(Clip clip, boolean maxInclusive) {
    return navigableMap.subMap(clip.min(), true, clip.max(), maxInclusive).entrySet().stream() //
        .map(entry -> new TsEntry(entry.getKey(), entry.getValue()));
  }

  @Override // from TimeSeries
  public final Tensor path() {
    return Tensor.of(navigableMap.entrySet().stream() //
        .map(entry -> Tensors.of(entry.getKey(), entry.getValue()))); // value is copied
  }

  @Override // from TimeSeries
  public TimeSeries extract(Clip clip) {
    return block(clip).copy();
  }

  @Override // from TimeSeries
  public TimeSeries block(Clip clip) {
    return new TimeSeriesImpl(navigableMap.subMap(clip.min(), true, clip.max(), true), resamplingMethod);
  }

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("TimeSeries", size(), resamplingMethod());
  }
}
