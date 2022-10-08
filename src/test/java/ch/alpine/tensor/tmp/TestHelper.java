// code by jph
package ch.alpine.tensor.tmp;

import java.util.List;

enum TestHelper {
  ;
  public static List<ResamplingMethod> list() {
    return List.of( //
        ResamplingMethods.LINEAR_INTERPOLATION, //
        ResamplingMethods.LINEAR_INTERPOLATION_SPARSE, //
        ResamplingMethods.HOLD_VALUE_FROM_LEFT, //
        ResamplingMethods.HOLD_VALUE_FROM_LEFT_SPARSE, //
        ResamplingMethods.HOLD_VALUE_FROM_RIGHT, //
        ResamplingMethods.HOLD_VALUE_FROM_RIGHT_SPARSE, //
        ResamplingMethods.NONE);
  }
}
