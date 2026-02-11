// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Clips;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/CoordinateBounds.html">CoordinateBounds</a>
 * 
 * @see CoordinateBoundingBox */
public enum CoordinateBounds {
  ;
  /** min and max are vectors of identical length
   * for instance to describe the 3-dimensional unit cube use
   * <pre>
   * min = {0, 0, 0}
   * max = {1, 1, 1}
   * </pre>
   * 
   * @param min lower left corner of axis aligned bounding box
   * @param max upper right corner of axis aligned bounding box
   * @return
   * @throws Exception if either input parameter is not a vector, or
   * if min and max have different lengths
   * @see Entrywise */
  public static CoordinateBoundingBox of(Tensor min, Tensor max) {
    return CoordinateBoundingBox.of(IntStream.range(0, Integers.requireEquals(min.length(), max.length())) //
        .mapToObj(index -> Clips.interval(min.Get(index), max.Get(index))));
  }

  /** @param tensor list of points
   * @return */
  public static CoordinateBoundingBox of(Tensor tensor) {
    return of( //
        Entrywise.min().of(tensor), //
        Entrywise.max().of(tensor));
  }

  /** @param cbb1
   * @param cbb2 with same dimensions as cbb1
   * @return smallest coordinate bounding box that covers the two given coordinate bounding boxes
   * @throws Exception if the two given coordinate bounding boxes are not compatible */
  public static CoordinateBoundingBox cover(CoordinateBoundingBox cbb1, CoordinateBoundingBox cbb2) {
    int n = Integers.requireEquals(cbb1.dimensions(), cbb2.dimensions());
    return CoordinateBoundingBox.of(IntStream.range(0, n) //
        .mapToObj(index -> Clips.cover( //
            cbb1.clip(index), //
            cbb2.clip(index))));
  }

  /** @param cbb1
   * @param cbb2
   * @return non-empty intersection or Optional.empty */
  public static Optional<CoordinateBoundingBox> optionalIntersection(CoordinateBoundingBox cbb1, CoordinateBoundingBox cbb2) {
    int n = Integers.requireEquals(cbb1.dimensions(), cbb2.dimensions());
    CoordinateBoundingBox cut = CoordinateBoundingBox.of(IntStream.range(0, n) //
        .mapToObj(i -> Clips.optionalIntersection(cbb1.clip(i), cbb2.clip(i))) //
        .flatMap(Optional::stream));
    return n == cut.dimensions() //
        ? Optional.of(cut)
        : Optional.empty();
  }
}
