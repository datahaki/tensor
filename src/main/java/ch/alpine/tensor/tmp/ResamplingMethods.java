// code by jph
package ch.alpine.tensor.tmp;

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
      navigableMap.put(key, value.copy());
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
      navigableMap.put(key, value.copy());
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
      if (Objects.isNull(navigableMap.higherKey(key))) {
        // compression depends only on key (not on value):
        Entry<Scalar, Tensor> tail = navigableMap.lowerEntry(key);
        if (Objects.nonNull(tail)) {
          Scalar tail_key = tail.getKey();
          Entry<Scalar, Tensor> head = navigableMap.lowerEntry(tail_key);
          if (Objects.nonNull(head) && head.getValue().equals(tail.getValue()))
            navigableMap.remove(tail_key);
        }
        // insertion:
        navigableMap.put(key, value.copy());
      } else {
        Entry<Scalar, Tensor> tail = navigableMap.floorEntry(key);
        if (Objects.isNull(tail) || !tail.getValue().equals(value))
          navigableMap.put(key, value.copy());
        // TODO compression higher is also possible
        /* otherwise dismiss given key value pair
         * since map is not empty and
         * floor entry has value identical to given value */
      }
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      if (Scalars.lessEquals(x, navigableMap.lastKey()))
        return navigableMap.floorEntry(x).getValue().copy();
      throw new Throw(x);
    }
  },
  /** in Mathematica: HoldValueFromRight */
  HOLD_HI {
    @Override // from ResamplingMethod
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      Entry<Scalar, Tensor> entry = navigableMap.ceilingEntry(key);
      if (Objects.isNull(entry) || !entry.getValue().equals(value))
        navigableMap.put(key, value.copy());
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
      navigableMap.put(key, value.copy());
    }

    @Override // from ResamplingMethod
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      return Objects.requireNonNull(navigableMap.get(x));
    }
  },
}
