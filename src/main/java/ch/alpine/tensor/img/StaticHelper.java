// code by jph
package ch.alpine.tensor.img;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/* package */ enum StaticHelper {
  ;
  /** there are only [0, 1, ..., 255] possible values for red, green, blue, and alpha.
   * We preallocate instances of these scalars in a lookup table to save memory and
   * possibly enhance execution time. */
  public static final Scalar[] LOOKUP = //
      IntStream.range(0, 256).mapToObj(RealScalar::of).toArray(Scalar[]::new);
}
