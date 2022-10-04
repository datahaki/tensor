// code by jph
package ch.alpine.tensor.tmp;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clips;

/** @see TimeSeries */
public enum ResamplingMethods implements ResamplingMethod {
  /** linear interpolation
   * default in Mathematica as "LinearInterpolation", and "{Interpolation, 1}" */
  LINEAR_INTERPOLATION {
    @Override // from ResamplingMethod
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      navigableMap.put(key, value);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      Entry<Scalar, Tensor> lo = navigableMap.floorEntry(x);
      Entry<Scalar, Tensor> hi = navigableMap.ceilingEntry(x);
      return LinearInterpolation.of(Tensors.of( //
          lo.getValue(), //
          hi.getValue())).at(Clips
              .interval( //
                  lo.getKey(), //
                  hi.getKey())
              .rescale(x));
    }
  },
  /** in Mathematica: HoldValueFromLeft */
  HOLD_LO {
    @Override // from ResamplingMethod
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      navigableMap.put(key, value);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      if (Scalars.lessEquals(x, navigableMap.lastKey()))
        return navigableMap.floorEntry(x).getValue().copy();
      throw new Throw(x);
    }
  },
  /** suitable for exact precision data sets */
  HOLD_LO_SPARSE {
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
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      if (Scalars.lessEquals(x, navigableMap.lastKey()))
        return navigableMap.floorEntry(x).getValue().copy();
      throw new Throw(x);
    }

    @Override // from ResamplingMethod
    public NavigableMap<Scalar, Tensor> pack(NavigableMap<Scalar, Tensor> navigableMap) {
      Entry<Scalar, Tensor> prev = navigableMap.firstEntry();
      if (Objects.nonNull(prev)) {
        Iterator<Entry<Scalar, Tensor>> iterator = //
            navigableMap.subMap(prev.getKey(), false, navigableMap.lastKey(), false) //
                .entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<Scalar, Tensor> next = iterator.next();
          if (prev.getValue().equals(next.getValue()))
            iterator.remove();
        }
      }
      return navigableMap;
    }
  },
  /** in Mathematica: HoldValueFromRight */
  HOLD_HI {
    @Override // from ResamplingMethod
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      Entry<Scalar, Tensor> entry = navigableMap.ceilingEntry(key);
      if (Objects.isNull(entry) || !entry.getValue().equals(value))
        navigableMap.put(key, value);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      if (Scalars.lessEquals(navigableMap.firstKey(), x))
        return navigableMap.ceilingEntry(x).getValue().copy();
      throw new Throw(x);
    }
  },
  /** in Mathematica: None */
  NONE {
    @Override // from ResamplingMethod
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      navigableMap.put(key, value);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      return Objects.requireNonNull(navigableMap.get(x));
    }
  },
}
