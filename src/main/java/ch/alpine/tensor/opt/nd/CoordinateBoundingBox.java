// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** n-dimensional axis aligned bounding box
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CoordinateBoundingBox.html">CoordinateBoundingBox</a>
 * 
 * @see CoordinateBounds
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class CoordinateBoundingBox implements Serializable {
  /** @param stream of clip instances
   * @return */
  public static CoordinateBoundingBox of(Stream<Clip> stream) {
    return new CoordinateBoundingBox(stream.toList());
  }

  /** @param clips
   * @return */
  public static CoordinateBoundingBox of(Clip... clips) {
    return new CoordinateBoundingBox(List.of(clips));
  }

  // ---
  private final List<Clip> list;

  private CoordinateBoundingBox(List<Clip> list) {
    this.list = list;
  }

  /** @return dimensions of bounding box */
  public int dimensions() {
    return list.size();
  }

  /** @param index in the range 0, 1, ... {@link #dimensions()} - 1
   * @return clip in given dimension */
  public Clip clip(int index) {
    return list.get(index);
  }

  /** @param vector of length {@link #dimensions()}
   * @return coordinates of vector clipped to bounds of this box instance */
  public Tensor mapInside(Tensor vector) {
    return Tensors.vector(i -> clip(i).apply(vector.Get(i)), //
        Integers.requireEquals(dimensions(), vector.length()));
  }

  /** @param vector
   * @return whether given vector is inside this bounding box */
  public boolean isInside(Tensor vector) {
    return IntStream.range(0, Integers.requireEquals(dimensions(), vector.length())) //
        .allMatch(i -> clip(i).isInside(vector.Get(i)));
  }

  /** @param vector
   * @return given vector
   * @throws Exception if any coordinate of vector is outside of this bounding box */
  public Tensor requireInside(Tensor vector) {
    if (isInside(vector))
      return vector;
    throw new Throw(vector);
  }

  // ---
  /** @param index of dimension
   * @return left, i.e. lower half of this bounding box */
  public CoordinateBoundingBox splitLo(int index) {
    List<Clip> copy = new ArrayList<>(list);
    Clip clip = clip(index);
    copy.set(index, Clips.interval(clip.min(), median(clip)));
    return new CoordinateBoundingBox(copy);
  }

  /** @param index of dimension
   * @return right, i.e. upper half of this bounding box */
  public CoordinateBoundingBox splitHi(int index) {
    List<Clip> copy = new ArrayList<>(list);
    Clip clip = clip(index);
    copy.set(index, Clips.interval(median(clip), clip.max()));
    return new CoordinateBoundingBox(copy);
  }

  /** @param index
   * @return median of bounds in dimension of given index */
  public Scalar median(int index) {
    return median(clip(index));
  }

  private static Scalar median(Clip clip) {
    return clip.min().add(clip.width().multiply(Rational.HALF));
  }

  // ---
  /** @return stream of all clips */
  public Stream<Clip> stream() {
    return list.stream();
  }

  /** @return lower left corner of bounding box */
  public Tensor min() {
    return Tensor.of(stream().map(Clip::min));
  }

  /** @return upper right corner of bounding box */
  public Tensor max() {
    return Tensor.of(stream().map(Clip::max));
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return list.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof CoordinateBoundingBox coordinateBoundingBox //
        && list.equals(coordinateBoundingBox.list);
  }

  @Override // from Object
  public String toString() {
    return list.toString();
  }
}
