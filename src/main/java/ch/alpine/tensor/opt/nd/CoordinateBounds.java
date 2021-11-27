// code by jph
package ch.alpine.tensor.opt.nd;

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
   * if vectors are different in length
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
}
