// code by jph
package ch.alpine.tensor.tmp;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarTensorFunction;

/* package */ class HoldHi extends BaseResamplingMethod {
  @Override // from ResamplingMethod
  public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
    Scalar first = navigableMap.firstKey();
    if (Scalars.lessEquals(first, x))
      return navigableMap.ceilingEntry(x).getValue().copy();
    throw new Throw(first, x);
  }

  @Override // from ResamplingMethod
  public Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
    Scalar first = navigableSet.first();
    if (Scalars.lessEquals(first, x))
      return function.apply(navigableSet.ceiling(x)).copy();
    throw new Throw(first, x);
  }

  @Override // from ResamplingMethod
  public Stream<Tensor> lines(NavigableMap<Scalar, Tensor> navigableMap) {
    return navigableMap.entrySet().stream() //
        .skip(1) //
        .map(next -> { //
          Entry<Scalar, Tensor> prev = navigableMap.lowerEntry(next.getKey()); //
          return Tensors.of( //
              Tensors.of(prev.getKey(), next.getValue()), //
              Tensors.of(next.getKey(), next.getValue()));
        });
  }

  @Override // from BaseResamplingMethod
  public String toString() {
    return "HoldValueFromRight";
  }
}
