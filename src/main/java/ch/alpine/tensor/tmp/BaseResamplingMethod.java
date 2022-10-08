// code by jph
package ch.alpine.tensor.tmp;

import java.io.Serializable;
import java.util.NavigableMap;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ abstract class BaseResamplingMethod implements ResamplingMethod, Serializable {
  @Override
  public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
    navigableMap.put(key, value);
  }

  /** @param navigableMap
   * @return given navigableMap but potentially with some entries removed */
  @Override
  public NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap) {
    return navigableMap;
  }

  @Override
  public abstract String toString();
}
