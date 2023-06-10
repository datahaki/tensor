// code by jph
package ch.alpine.tensor.tmp;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clips;

/* package */ class Linear extends BaseResamplingMethod {
  @Override // from ResamplingMethod
  public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
    Entry<Scalar, Tensor> e_lo = navigableMap.floorEntry(x);
    Scalar lo = e_lo.getKey();
    if (lo.equals(x))
      return e_lo.getValue().copy();
    Entry<Scalar, Tensor> e_hi = navigableMap.ceilingEntry(x);
    return LinearInterpolation.of(Tensors.of( //
        e_lo.getValue(), //
        e_hi.getValue())).at(Clips.interval(lo, e_hi.getKey()).rescale(x));
  }

  @Override // from ResamplingMethod
  public Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
    Scalar lo = navigableSet.floor(x);
    Tensor f_lo = function.apply(lo);
    if (lo.equals(x))
      return f_lo;
    Scalar hi = navigableSet.ceiling(x);
    return LinearInterpolation.of(Tensors.of(f_lo, function.apply(hi))).at(Clips.interval(lo, hi).rescale(x));
  }

  @Override
  public Stream<Tensor> lines(NavigableMap<Scalar, Tensor> navigableMap) {
    return Stream.of(Tensor.of(navigableMap.entrySet().stream() //
        .map(entry -> Tensors.of(entry.getKey(), entry.getValue())))); // value is copied
  }

  @Override
  public String toString() {
    return "LinearInterpolation";
  }
}
