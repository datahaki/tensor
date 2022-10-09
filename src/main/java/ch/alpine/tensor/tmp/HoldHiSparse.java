// code by jph
package ch.alpine.tensor.tmp;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ class HoldHiSparse extends HoldHi {
  @Override // from ResamplingMethod
  public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
    Entry<Scalar, Tensor> entry = navigableMap.ceilingEntry(key);
    if (Objects.isNull(entry) || !entry.getValue().equals(value))
      navigableMap.put(key, value);
  }

  @Override
  public NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap) {
    // TODO TENSOR IMPL
    return super.pack(navigableMap);
  }

  @Override
  public String toString() {
    return "HoldValueFromRightSparse";
  }
}
