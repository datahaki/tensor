// code by jph
package ch.alpine.tensor.tmp;

import java.util.Objects;

/** @see TimeSeries */
public enum ResamplingMethods {
  LINEAR_INTERPOLATION,
  LINEAR_INTERPOLATION_SPARSE,
  HOLD_VALUE_FROM_LEFT,
  HOLD_VALUE_FROM_LEFT_SPARSE,
  HOLD_VALUE_FROM_RIGHT,
  HOLD_VALUE_FROM_RIGHT_SPARSE,
  NONE, //
  ;

  private final ResamplingMethod resamplingMethod;

  ResamplingMethods() {
    try {
      this.resamplingMethod = (ResamplingMethod) Objects.requireNonNull(ResamplingMethod.class.getField(name()).get(null));
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  public ResamplingMethod get() {
    return resamplingMethod;
  }
}
