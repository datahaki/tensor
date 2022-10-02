// code by jph
package ch.alpine.tensor.sca;

import java.util.SortedMap;
import java.util.SortedSet;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.jet.CenteredInterval;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.red.MinMax;

/** factory for the creation of {@link Clip}
 * 
 * Remark:
 * A {@link Clip} represents a non-empty, closed interval of the form [min, max].
 * The values min and max can be real numbers, or instances of {@link Quantity}
 * with identical {@link Unit}. */
public enum Clips {
  ;
  /** clips in the interval [min, ..., max]
   * 
   * @param min
   * @param max equals or greater than given min
   * @return function that clips the input to the closed interval [min, max]
   * @throws Exception if min is greater than max
   * @throws Exception if min and max give different {@link QuantityUnit} */
  public static Clip interval(Scalar min, Scalar max) {
    Scalars.compare(min, max); // assert that min and max have identical units
    return create(min, max);
  }

  /** @param min
   * @param max equals or greater than given min
   * @return function that clips the input to the closed interval [min, max]
   * @throws Exception if min is greater than max */
  public static Clip interval(Number min, Number max) {
    return create(RealScalar.of(min), RealScalar.of(max));
  }

  // ---
  /** clips in the interval [0, ..., max]
   * 
   * @param max non-negative
   * @return function that clips the input to the closed interval [0, max]
   * @throws Exception if max is negative */
  public static Clip positive(Scalar max) {
    return create(max.zero(), max);
  }

  /** @param max non-negative
   * @return function that clips the input to the closed interval [0, max]
   * @throws Exception if max is negative */
  public static Clip positive(Number max) {
    return positive(RealScalar.of(max));
  }

  // ---
  /** clips in the interval [-max, ..., max]
   * 
   * @param max non-negative
   * @return function that clips the input to the closed interval [-max, max]
   * @throws Exception if max is negative */
  public static Clip absolute(Scalar max) {
    return create(max.negate(), max);
  }

  /** @param max non-negative
   * @return function that clips the input to the closed interval [-max, max]
   * @throws Exception if max is negative */
  public static Clip absolute(Number max) {
    return absolute(RealScalar.of(max));
  }

  // ---
  /** @param center
   * @param radius non-negative
   * @return clip over the interval [center-radius, ..., center+radius]
   * @see CenteredInterval */
  public static Clip centered(Scalar center, Scalar radius) {
    return create(center.subtract(radius), center.add(radius));
  }

  /** @param center
   * @param radius non-negative
   * @return clip over the interval [center-radius, ..., center+radius]
   * @see CenteredInterval */
  public static Clip centered(Number center, Number radius) {
    return centered(RealScalar.of(center), RealScalar.of(radius));
  }

  // ---
  /** Careful:
   * only use on sets that are sorted according to
   * {@link Scalars#compare(Scalar, Scalar)}
   * 
   * @param sortedSet with canonic comparator, i.e. {@link Scalars#compare(Scalar, Scalar)}
   * @return
   * @see MinMax#toClip() */
  public static <K extends Scalar> Clip setcover(SortedSet<K> sortedSet) {
    return interval( //
        sortedSet.first(), //
        sortedSet.last());
  }

  /** Careful:
   * only use on maps that are sorted according to
   * {@link Scalars#compare(Scalar, Scalar)}
   * 
   * @param sortedMap with canonic comparator, i.e. {@link Scalars#compare(Scalar, Scalar)}
   * @return
   * @throws Exception if given sortedMap is empty
   * @see MinMax#toClip() */
  public static <K extends Scalar> Clip keycover(SortedMap<K, ?> sortedMap) {
    return interval( //
        sortedMap.firstKey(), //
        sortedMap.lastKey());
  }

  // ---
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

  // ---
  /** @param clip1
   * @param clip2
   * @return [max(clip1.min, clip2.min), min(clip1.max, clip2.max)], i.e.
   * the largest interval that is covered by both input intervals
   * @throws Exception if resulting intersection is empty */
  public static Clip intersection(Clip clip1, Clip clip2) {
    return create( //
        Max.of(clip1.min(), clip2.min()), //
        Min.of(clip1.max(), clip2.max()));
  }

  /** @param clip1
   * @param clip2
   * @return [min(clip1.min, clip2.min), max(clip1.max, clip2.max)], i.e.
   * the smallest interval that covers both input intervals */
  public static Clip cover(Clip clip1, Clip clip2) {
    return create( //
        Min.of(clip1.min(), clip2.min()), //
        Max.of(clip1.max(), clip2.max()));
  }

  // ---
  // helper function
  private static Clip create(Scalar min, Scalar max) {
    Scalar width = max.subtract(min);
    return min.equals(max) //
        ? new ClipPoint(min, width)
        : new ClipInterval(min, max, Sign.requirePositive(width));
  }
}
