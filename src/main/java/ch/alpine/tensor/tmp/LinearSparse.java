// code by jph
package ch.alpine.tensor.tmp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ class LinearSparse extends Linear {
  @Override // from ResamplingMethod
  public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
    navigableMap.put(key, value);
    Entry<Scalar, Tensor> cand = navigableMap.lowerEntry(key);
    if (Objects.nonNull(cand) && cand.getValue().equals(value)) {
      Entry<Scalar, Tensor> head = navigableMap.lowerEntry(cand.getKey());
      if (Objects.nonNull(head) && head.getValue().equals(cand.getValue()))
        navigableMap.remove(cand.getKey());
    }
  }

  @Override // from ResamplingMethod
  public NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap) {
    if (2 < navigableMap.size()) {
      Entry<Scalar, Tensor> head = navigableMap.firstEntry();
      Entry<Scalar, Tensor> cand = navigableMap.higherEntry(head.getKey());
      Set<Scalar> set = new HashSet<>();
      Iterator<Entry<Scalar, Tensor>> iterator = //
          navigableMap.subMap(cand.getKey(), false, navigableMap.lastKey(), true) //
              .entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<Scalar, Tensor> tail = iterator.next();
        if (head.getValue().equals(cand.getValue()) && //
            cand.getValue().equals(tail.getValue()))
          set.add(cand.getKey());
        head = cand;
        cand = tail;
      }
      set.forEach(navigableMap::remove);
    }
    return navigableMap;
  }

  @Override
  public String toString() {
    return "LinearInterpolationSparse";
  }
}
