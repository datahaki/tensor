// code by jph
package ch.alpine.tensor.tmp;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarTensorFunction;
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
  },
  /** in Mathematica: HoldValueFromLeft */
  HOLD_LO {
    @Override // from ResamplingMethod
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      navigableMap.put(key, value);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      Scalar last = navigableMap.lastKey();
      if (Scalars.lessEquals(x, last))
        return navigableMap.floorEntry(x).getValue().copy();
      throw new Throw(last, x);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
      Scalar last = navigableSet.last();
      if (Scalars.lessEquals(x, last))
        return function.apply(navigableSet.floor(x)).copy();
      throw new Throw(last, x);
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
      Scalar last = navigableMap.lastKey();
      if (Scalars.lessEquals(x, last))
        return navigableMap.floorEntry(x).getValue().copy();
      throw new Throw(last, x);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
      Scalar last = navigableSet.last();
      if (Scalars.lessEquals(x, last))
        return function.apply(navigableSet.floor(x)).copy();
      throw new Throw(last, x);
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
      Scalar first = navigableMap.firstKey();
      if (Scalars.lessEquals(first, x))
        return navigableMap.ceilingEntry(x).getValue().copy();
      throw new Throw(first, x);
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
      if (Scalars.lessEquals(navigableSet.first(), x))
        return function.apply(navigableSet.ceiling(x)).copy();
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

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableSet<Scalar> navigableSet, ScalarTensorFunction function, Scalar x) {
      if (navigableSet.contains(x))
        return Objects.requireNonNull(function.apply(x));
      throw new Throw(x);
    }
  },
}
