// code by jph
package ch.alpine.tensor.alg;

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

  /** The scalar entries of the given tensor may also be instance of
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

  // helper function
  private static Tensor _result(Tensor tensor, MinMax minMax) {
    if (0 < minMax.getCount()) {
      Clip clip = minMax.getClip();
      if (Scalars.nonZero(clip.width()))
        // operation is not identical to Clip#rescale for non-finite values
        return tensor.map(scalar -> scalar.subtract(clip.min()).divide(clip.width()));
    }
    // set all finite number entries to 0, but keep non-finite values
    return tensor.map(FINITE_NUMBER_ZERO);
  }

  // ---
  private final MinMax minMax;
  private final Tensor result;

  /** @param tensor of rank at least 1
   * @throws Exception if given tensor is a scalar */
  public Rescale(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    minMax = tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .filter(FiniteScalarQ::of) //
        .collect(MinMax.collector());
    result = _result(tensor, minMax);
  }

  public MinMax minMax() {
    return minMax;
  }

  public Tensor result() {
    return result;
  }
}
