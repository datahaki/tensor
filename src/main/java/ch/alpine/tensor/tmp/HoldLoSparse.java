// code by jph
package ch.alpine.tensor.tmp;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ class HoldLoSparse extends HoldLo {
  @Override // from ResamplingMethod
  public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
    // if given key is higher than existing max key, or map is empty
    if (Objects.isNull(navigableMap.higherKey(key))) {
      // compression operates on the "tail" key lower than the given key,
      // compression does not depend on given value
      Entry<Scalar, Tensor> tail = navigableMap.lowerEntry(key);
      if (Objects.nonNull(tail)) {
        Scalar tail_key = tail.getKey();
        Entry<Scalar, Tensor> head = navigableMap.lowerEntry(tail_key);
        if (Objects.nonNull(head) && head.getValue().equals(tail.getValue()))
          navigableMap.remove(tail_key);
      }
      // insertion of given (key, value)-pair
      navigableMap.put(key, value);
    } else { // key is smaller, or equal to existing highest key
      Entry<Scalar, Tensor> tail = navigableMap.floorEntry(key);
      // insert only if no floor element exist, or floor value is not equal to given value
      if (Objects.isNull(tail) || !tail.getValue().equals(value))
        navigableMap.put(key, value);
      /* otherwise dismiss given (key, value)-pair, since key is not maximal, and
       * floor entry has value identical to given value
       * Remark: more compression by inspecting higher than key entries would be possible */
    }
  }

  @Override // from ResamplingMethod
  public NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap) {
    Entry<Scalar, Tensor> first = navigableMap.firstEntry();
    if (Objects.nonNull(first)) {
      // first and last element are never to be removed
      Iterator<Entry<Scalar, Tensor>> iterator = //
          navigableMap.subMap(first.getKey(), false, navigableMap.lastKey(), false) //
              .entrySet().iterator();
      Tensor head = first.getValue();
      while (iterator.hasNext()) {
        Tensor tail = iterator.next().getValue();
        if (head.equals(tail))
          iterator.remove();
        else
          head = tail; // new reference for comparison
      }
    }
    return navigableMap;
  }

  @Override
  public String toString() {
    return "HoldValueFromLeftSparse";
  }
}
