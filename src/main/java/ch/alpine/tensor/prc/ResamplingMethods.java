// code by jph
package ch.alpine.tensor.prc;

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
   * default in Mathematica as {Interpolation, 1} */
  INTERPOLATION_1 {
    @Override
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      navigableMap.put(key, value.copy());
    }

    @Override
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
    @Override
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      Entry<Scalar, Tensor> entry = navigableMap.floorEntry(key);
      if (Objects.isNull(entry) || !entry.getValue().equals(value))
        navigableMap.put(key, value.copy());
      // FIXME TENSOR extend! to key
      /* otherwise dismiss given key value pair
       * since map is not empty and
       * floor entry has value identical to given value */
    }

    @Override
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      if (Scalars.lessEquals(x, navigableMap.lastKey()))
        return navigableMap.floorEntry(x).getValue().copy();
      throw new Throw(x);
    }
  },
  HOLD_HI {
    @Override
    public void insert(NavigableMap<Scalar, Tensor> navigableMap, Scalar key, Tensor value) {
      // TODO TENSOR IMPL
      throw new UnsupportedOperationException();
    }

    @Override
    public Tensor evaluate(NavigableMap<Scalar, Tensor> navigableMap, Scalar x) {
      // TODO TENSOR TEST
      if (Scalars.lessEquals(navigableMap.firstKey(), x))
        return navigableMap.ceilingEntry(x).getValue().copy();
      throw new Throw(x);
    }
  },
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
  // TODO TENSOR IMPL NEAREST
}
