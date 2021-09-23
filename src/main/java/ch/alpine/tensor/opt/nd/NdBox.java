// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** axis aligned bounding box
 * immutable */
public class NdBox implements Serializable {
  /** @param lBounds lower left corner of axis aligned bounding box
   * @param uBounds upper right corner of axis aligned bounding box
   * @return
   * @throws Exception if either input parameter is not a vector, or
   * if vectors are different in length */
  public static NdBox of(Tensor lBounds, Tensor uBounds) {
    StaticHelper.require(lBounds, uBounds);
    return new NdBox(IntStream.range(0, lBounds.length()) //
        .mapToObj(index -> Clips.interval( //
            lBounds.Get(index), //
            uBounds.Get(index)))
        .collect(Collectors.toList()));
  }

  // ---
  private final List<Clip> list;

  private NdBox(List<Clip> clips) {
    this.list = clips;
  }

  public Clip clip(int index) {
    return list.get(index);
  }

  /** @param index of dimension
   * @return left, i.e. lower half of this bounding box */
  public NdBox deriveL(int index) {
    List<Clip> copy = new ArrayList<>(list);
    Clip clip = this.list.get(index);
    copy.set(index, Clips.interval(clip.min(), median(clip)));
    return new NdBox(copy);
  }

  /** @param index of dimension
   * @return right, i.e. upper half of this bounding box */
  public NdBox deriveR(int index) {
    List<Clip> copy = new ArrayList<>(list);
    Clip clip = this.list.get(index);
    copy.set(index, Clips.interval(median(clip), clip.max()));
    return new NdBox(copy);
  }

  /** @param index
   * @return median of bounds in dimension of given index */
  public Scalar median(int index) {
    return median(list.get(index));
  }

  private static Scalar median(Clip clip) {
    return clip.min().add(clip.max()).multiply(RationalScalar.HALF);
  }

  /** @param vector
   * @return whether given vector is inside this bounding box */
  public boolean isInside(Tensor vector) {
    if (vector.length() == list.size()) {
      AtomicInteger atomicInteger = new AtomicInteger();
      return list.stream() //
          .allMatch(clip -> clip.isInside(vector.Get(atomicInteger.getAndIncrement())));
    }
    throw TensorRuntimeException.of(vector);
  }

  /** @param vector
   * @return given vector
   * @throws Exception if any coordinate of vector is outside of this bounding box */
  public Tensor requireInside(Tensor vector) {
    if (isInside(vector))
      return vector;
    throw TensorRuntimeException.of(vector);
  }

  /** @param vector
   * @return coordinates of vector clipped to bounds of this instance */
  public Tensor clip(Tensor vector) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return Tensor.of(list.stream().map(clip -> clip.apply(vector.Get(atomicInteger.getAndIncrement()))));
  }

  /** @return lower left corner of bounding box */
  public Tensor lBounds() {
    return Tensor.of(list.stream().map(Clip::min));
  }

  /** @return upper right corner of bounding box */
  public Tensor uBounds() {
    return Tensor.of(list.stream().map(Clip::max));
  }

  public int dimensions() {
    return list.size();
  }
}
