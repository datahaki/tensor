// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** axis aligned bounding box */
public class NdBounds {
  final Tensor lBounds;
  final Tensor uBounds;
  // final List<Clip> clips;

  public NdBounds(Tensor lBounds, Tensor uBounds) {
    this.lBounds = lBounds.copy();
    this.uBounds = uBounds.copy();
    // clips = IntStream.range(0, lBounds.length()) //
    // .mapToObj(index -> Clips.interval( //
    // lBounds.Get(index), //
    // uBounds.Get(index)))
    // .collect(Collectors.toList());
  }

  public Tensor lBounds() {
    return lBounds.copy();
  }

  public Tensor uBounds() {
    return uBounds.copy();
  }

  /** @param index
   * @return median of bounds in dimension of given index */
  public Scalar median(int index) {
    // clips.get(index).min().add(clips.get(index).max())
    return lBounds.Get(index).add(uBounds.Get(index)).multiply(RationalScalar.HALF);
  }

  public Clip clip(int index) {
    return Clips.interval( //
        lBounds.Get(index), //
        uBounds.Get(index));
  }

  /** @param vector
   * @return coordinates of vector clipped to bounds of this instance */
  public Tensor clip(Tensor vector) {
    return Tensors.vector(i -> clip(i).apply(vector.Get(i)), vector.length());
  }
}
