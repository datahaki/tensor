// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;

/** factory for the creation of {@link Clip} */
public enum Clips {
  ;
  /** clips in the interval [min, ..., max]
   * 
   * @param min
   * @param max
   * @return function that clips the input to the closed interval [min, max]
   * @throws Exception if min is greater than max */
  public static Clip interval(Scalar min, Scalar max) {
    Scalar width = max.subtract(min);
    return min.equals(max) //
        ? new ClipPoint(min, width)
        : new ClipInterval(min, max, Sign.requirePositive(width));
  }

  /** @param min
   * @param max
   * @return function that clips the input to the closed interval [min, max]
   * @throws Exception if min is greater than max */
  public static Clip interval(Number min, Number max) {
    return interval(RealScalar.of(min), RealScalar.of(max));
  }

  /***************************************************/
  /** clips in the interval [0, ..., max]
   * 
   * @param max non-negative
   * @return function that clips the input to the closed interval [0, max]
   * @throws Exception if max is negative */
  public static Clip positive(Scalar max) {
    return interval(max.zero(), max);
  }

  /** @param max non-negative
   * @return function that clips the input to the closed interval [0, max]
   * @throws Exception if max is negative */
  public static Clip positive(Number max) {
    return positive(RealScalar.of(max));
  }

  /***************************************************/
  /** clips in the interval [-max, ..., max]
   * 
   * @param max non-negative
   * @return function that clips the input to the closed interval [-max, max]
   * @throws Exception if max is negative */
  public static Clip absolute(Scalar max) {
    return interval(max.negate(), max);
  }

  /** @param max non-negative
   * @return function that clips the input to the closed interval [-max, max]
   * @throws Exception if max is negative */
  public static Clip absolute(Number max) {
    return absolute(RealScalar.of(max));
  }

  /***************************************************/
  private static final Clip UNIT = positive(1);
  private static final Clip ABSOLUTE_ONE = absolute(1);

  /** @return function that clips a scalar to the unit interval [0, 1] */
  public static Clip unit() {
    return UNIT;
  }

  /** @return function that clips a scalar to the interval [-1, 1] */
  public static Clip absoluteOne() {
    return ABSOLUTE_ONE;
  }

  /***************************************************/
  /** @param clip1
   * @param clip2
   * @return [max(clip1.min, clip2.min), min(clip1.max, clip2.max)], i.e.
   * the largest interval that is covered by both input intervals
   * @throws Exception if resulting intersection is empty */
  public static Clip intersection(Clip clip1, Clip clip2) {
    return Clips.interval( //
        Max.of(clip1.min(), clip2.min()), //
        Min.of(clip1.max(), clip2.max()));
  }

  /** @param clip1
   * @param clip2
   * @return [min(clip1.min, clip2.min), max(clip1.max, clip2.max)], i.e.
   * the smallest interval that covers both input intervals */
  public static Clip cover(Clip clip1, Clip clip2) {
    return Clips.interval( //
        Min.of(clip1.min(), clip2.min()), //
        Max.of(clip1.max(), clip2.max()));
  }
}
