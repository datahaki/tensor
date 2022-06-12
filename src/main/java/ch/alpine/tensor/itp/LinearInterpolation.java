// code by jph
package ch.alpine.tensor.itp;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Floor;

/** multi-linear interpolation
 * 
 * <p>valid input for a respective dimension d are in the closed interval
 * <code>[0, Dimensions.of(tensor).get(d) - 1]</code>
 * 
 * <p>Remark: for scalar inverse linear interpolation use {@link Clip#rescale(Scalar)} */
public class LinearInterpolation extends AbstractInterpolation implements Serializable {
  /** @param tensor
   * @return
   * @throws Exception if tensor == null */
  public static Interpolation of(Tensor tensor) {
    return new LinearInterpolation(tensor);
  }

  /** @param clip
   * @return interpolation for evaluation over the unit interval with
   * values ranging linearly between given clip.min and clip.max */
  public static Interpolation of(Clip clip) {
    return of(Tensors.of(clip.min(), clip.max()));
  }

  // ---
  private final Tensor tensor;

  private LinearInterpolation(Tensor tensor) {
    this.tensor = Objects.requireNonNull(tensor);
  }

  @Override // from Interpolation
  public Tensor get(Tensor index) {
    if (Tensors.isEmpty(index))
      return tensor.copy();
    Tensor floor = Floor.of(index);
    Tensor above = Ceiling.of(index);
    Tensor width = above.subtract(floor).map(RealScalar.ONE::add);
    List<Integer> fromIndex = Primitives.toListInteger(floor);
    List<Integer> dimensions = Primitives.toListInteger(width);
    Tensor block = tensor.block(fromIndex, dimensions);
    Tensor weights = Transpose.of(Tensors.of( //
        above.subtract(index), //
        index.subtract(floor)));
    for (Tensor weight : weights)
      block = block.length() == 1 //
          ? block.get(0)
          : weight.dot(block);
    return block;
  }

  @Override // from Interpolation
  public Tensor at(Scalar index) {
    Scalar floor = Floor.FUNCTION.apply(index);
    Scalar remain = index.subtract(floor);
    int below = Scalars.intValueExact(floor);
    if (Scalars.isZero(remain))
      return tensor.get(below);
    return Tensors.of(remain.one().subtract(remain), remain) //
        .dot(tensor.block(List.of(below), List.of(2)));
  }
}
