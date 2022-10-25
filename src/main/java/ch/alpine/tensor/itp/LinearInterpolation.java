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
import ch.alpine.tensor.api.ScalarBinaryOperator;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
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
   * @return interpolation for evaluation over the unit interval [0, 1] with
   * values ranging linearly between given clip.min and clip.max. values
   * outside [0, 1] cause an Exception to be thrown */
  // public static ScalarUnaryOperator of(Clip clip) {
  // return (ExactScalarQ.of(clip.min()) && ExactScalarQ.of(clip.max())) //
  // || Scalars.isZero(clip.width()) //
  // ? ratio -> clip.min().add(clip.width().multiply(Clips.unit().requireInside(ratio)))
  // : ratio -> clip.min().multiply(RealScalar.ONE.subtract(ratio)) //
  // .add(clip.max().multiply(Clips.unit().requireInside(ratio)));
  // }
  public static ScalarUnaryOperator of(Clip clip) {
    Objects.requireNonNull(clip);
    return ratio -> ratio.equals(RealScalar.ONE) //
        ? clip.max()
        : clip.min().add(clip.width().multiply(Clips.unit().requireInside(ratio)));
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
    for (Tensor weight : index.subtract(floor))
      block = block.length() == 1 //
          ? block.get(0)
          : Entrywise.with(interp(weight)).apply(block.get(0), block.get(1));
    return block;
  }

  @Override // from Interpolation
  public Tensor at(Scalar index) {
    Scalar floor = Floor.FUNCTION.apply(index);
    Scalar weight = index.subtract(floor);
    int below = Scalars.intValueExact(floor);
    return Scalars.isZero(weight) //
        ? tensor.get(below)
        : Entrywise.with(interp(weight)).apply(tensor.get(below), tensor.get(below + 1));
  }

  /** @param ratio scalar should be in semi-open interval [0, 1)
   * @return
   * @throws Exception if given ratio is not a {@link Scalar} */
  private static ScalarBinaryOperator interp(Tensor ratio) {
    return (a, b) -> a.add(ratio.multiply(b.subtract(a)));
  }
}
