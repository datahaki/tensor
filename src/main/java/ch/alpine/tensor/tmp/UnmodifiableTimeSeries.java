// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableMap;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;

/* package */ class UnmodifiableTimeSeries extends TimeSeriesImpl {
  public UnmodifiableTimeSeries(NavigableMap<Scalar, Tensor> navigableMap, ResamplingMethod resamplingMethod) {
    super(navigableMap, resamplingMethod);
  }

  @Override // from TimeSeriesImpl
  public TimeSeries unmodifiable() {
    return this;
  }

  @Override // from TimeSeriesImpl
  public void insert(Scalar key, Tensor value) {
    throw new UnsupportedOperationException("unmodifiable");
  }

  @Override // from TimeSeriesImpl
  public Stream<TsEntry> stream(Clip clip, boolean maxInclusive) {
    return navigableMap.subMap(clip.min(), true, clip.max(), maxInclusive).entrySet().stream() //
        .map(entry -> new TsEntry(entry.getKey(), entry.getValue().unmodifiable()));
  }
}
