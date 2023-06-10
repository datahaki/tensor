// code by jph
package ch.alpine.tensor.tmp;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarTensorFunction;

/* package */ final class None extends BaseResamplingMethod {
  @Override // from ResamplingMethod
  public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
    return Objects.requireNonNull(navigableMap.get(x));
  }

  @Override // from ResamplingMethod
  public Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
    if (navigableSet.contains(x))
      return Objects.requireNonNull(function.apply(x));
    throw new Throw(x);
  }

  @Override
  public Stream<Tensor> lines(NavigableMap<Scalar, Tensor> navigableMap) {
    return Stream.empty();
  }

  @Override
  public String toString() {
    return "None";
  }
}
