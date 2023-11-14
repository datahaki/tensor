// code by jph
package ch.alpine.tensor.alg;

import java.util.Objects;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.chq.ScalarQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.MinMax;
import ch.alpine.tensor.sca.Clip;

/** Rescale so that all the list elements run from 0 to 1
 * 
 * <code>
 * Rescale[{-0.7, 0.5, 1.2, 5.6, 1.8}] == {0., 0.190476, 0.301587, 1., 0.396825}
 * </code>
 * 
 * <p>Mathematica handles Infinity in a non-trivial way.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Rescale.html">Rescale</a> */
public class Rescale {
  /** RealScalar.ZERO is used instead of {@link Scalar#zero()}
   * to eliminate unit of {@link Quantity}. */
  private static final ScalarUnaryOperator FINITE_NUMBER_ZERO = //
      scalar -> FiniteScalarQ.of(scalar) ? RealScalar.ZERO : scalar;

  /** The scalar entries of the given tensor may also be an instance of
   * {@link Quantity} with identical unit.
   * 
   * <p>Example:
   * <pre>
   * Rescale[{10, 20, 30}] == {0, 1/2, 1}
   * Rescale[{3[s], Infinity[s], 6[s], 2[s]}] == {1/4, Infinity, 1, 0}
   * </pre>
   * 
   * @param tensor of arbitrary structure
   * @return
   * @throws Exception if any entry is a {@link ComplexScalar} */
  public static Tensor of(Tensor tensor) {
    return new Rescale(tensor).result();
  }

  // ---
  private final Clip clip;
  private final Tensor result;

  /** @param tensor of rank at least 1
   * @param clip may be null
   * @throws Exception if given tensor is a scalar */
  public Rescale(Tensor tensor, Clip clip) {
    ScalarQ.thenThrow(tensor);
    this.clip = clip;
    result = tensor.map(Objects.nonNull(clip) && Scalars.nonZero(clip.width()) //
        // operation is not identical to Clip#rescale for non-finite values
        ? scalar -> scalar.subtract(clip.min()).divide(clip.width())
        // set all finite number entries to 0, but keep non-finite values
        : FINITE_NUMBER_ZERO);
  }

  /** @param tensor of rank at least 1
   * @throws Exception if given tensor is a scalar */
  public Rescale(Tensor tensor) {
    this(tensor, Flatten.scalars(tensor) //
        .filter(FiniteScalarQ::of) //
        .collect(MinMax.toClip()));
  }

  /** @return interval that tightly contains the finite scalars in the given tensor,
   * i.e. that satisfy {@link FiniteScalarQ}, or null if given tensor contains none
   * such scalars. */
  public Clip clip() {
    return clip;
  }

  /** @return tensor with the same structure as given tensor but all finite scalar
   * entries scaled to the unit interval, and non-finite scalar entries as-is. */
  public Tensor result() {
    return result;
  }
}
