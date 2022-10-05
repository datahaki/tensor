// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableSet;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TimeSeriesWrap implements TimeSeries {
  private final NavigableSet<Scalar> navigableSet;
  private final ScalarTensorFunction function;
  private final ResamplingMethod resamplingMethod;

  public TimeSeriesWrap(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, ResamplingMethod resamplingMethod) {
    this.navigableSet = navigableSet;
    this.function = function;
    this.resamplingMethod = resamplingMethod;
  }

  @Override
  public ResamplingMethod resamplingMethod() {
    return resamplingMethod;
  }

  @Override
  public TimeSeries unmodifiable() {
    return this;
  }

  @Override
  public TimeSeries copy() {
    return this; // TODO TENSOR consider
  }

  @Override
  public void insert(Scalar key, Tensor value) {
    throw new Throw(this);
  }

  @Override
  public Tensor evaluate(Scalar x) {
    return resamplingMethod.evaluate(navigableSet, function, x);
  }

  @Override
  public Clip domain() {
    return Clips.setcover(navigableSet);
  }

  @Override
  public int size() {
    return navigableSet.size();
  }

  @Override
  public boolean isEmpty() {
    return navigableSet.isEmpty();
  }

  @Override
  public NavigableSet<Scalar> keySet(Clip clip, boolean maxInclusive) {
    return navigableSet.subSet(clip.min(), true, clip.max(), maxInclusive);
  }

  @Override
  public Stream<TsEntry> stream() {
    return navigableSet.stream() //
        .map(key -> new TsEntry(key, function.apply(key)));
  }

  @Override
  public TimeSeries block(Clip clip, boolean maxInclusive) {
    return new TimeSeriesWrap(navigableSet.subSet(clip.min(), true, clip.max(), maxInclusive), function, resamplingMethod);
  }

  @Override // from TimeSeries
  public final Tensor path() {
    return Tensor.of(navigableSet.stream() //
        .map(key -> Tensors.of(key, function.apply(key)))); // value is copied
  }

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("TimeSeries", size(), resamplingMethod());
  }
}
