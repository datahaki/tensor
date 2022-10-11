// code by jph
package ch.alpine.tensor.tmp;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ class HoldHiSparse extends HoldHi {
  @Override // from BaseResamplingMethod
  public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
    Entry<Scalar, Tensor> entry = navigableMap.ceilingEntry(key);
    if (Objects.isNull(entry) || !entry.getValue().equals(value))
      navigableMap.put(key, value);
  }

  @Override // from BaseResamplingMethod
  public NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap) {
    if (2 < navigableMap.size()) {
      Set<Scalar> set = new HashSet<>();
      Entry<Scalar, Tensor> prev = navigableMap.higherEntry(navigableMap.firstKey());
      for (Entry<Scalar, Tensor> next : //
      navigableMap.subMap(prev.getKey(), false, navigableMap.lastKey(), true) //
          .entrySet()) {
        if (prev.getValue().equals(next.getValue()))
          set.add(prev.getKey());
        prev = next;
      }
      set.forEach(navigableMap::remove);
    }
    return navigableMap;
  }

  @Override // from HoldHi
  public String toString() {
    return "HoldValueFromRightSparse";
  }
}
