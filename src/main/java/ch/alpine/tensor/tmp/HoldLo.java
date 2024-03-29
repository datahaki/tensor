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

/* package */ class HoldLo extends BaseResamplingMethod {
  @Override // from ResamplingMethod
  public final Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
    Scalar last = navigableMap.lastKey();
    if (Scalars.lessEquals(x, last))
      return navigableMap.floorEntry(x).getValue().copy();
    throw new Throw(last, x);
  }

  @Override // from ResamplingMethod
  public final Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
    Scalar last = navigableSet.last();
    if (Scalars.lessEquals(x, last))
      return function.apply(navigableSet.floor(x)).copy();
    throw new Throw(last, x);
  }

  @Override // from ResamplingMethod
  public Stream<Tensor> lines(NavigableMap<Scalar, Tensor> navigableMap) {
    return navigableMap.entrySet().stream() //
        .skip(1) //
        .map(next -> { //
          Entry<Scalar, Tensor> prev = navigableMap.lowerEntry(next.getKey()); //
          return Tensors.of( //
              Tensors.of(prev.getKey(), prev.getValue()), //
              Tensors.of(next.getKey(), prev.getValue()));
        });
  }

  @Override
  public String toString() {
    return "HoldValueFromLeft";
  }
}
