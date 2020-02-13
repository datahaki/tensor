// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.OrderedQ;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Quantile of the tensor library only operates on vectors.
 * 
 * Mathematica::Quantile also operates (trivially) on matrices.
 * 
 * Quantile does not average as {@link Median}:
 * <code>Quantile[{1, 2}, 0.5] == 1</code>,
 * <code>Median[{1, 2}] == 3/2</code>
 * 
 * <p>for vector input, the implementation is compliant to Mathematica.
 * Mathematica::Quantile also allows matrices as input.
 * 
 * <p>Function arguments are required to be in the interval [0, 1].
 * 
 * <p>Example:
 * <code>Quantile[{0, 1, 2, 3, 4}, {0, 1/5, 2/5, 1}] == {0, 0, 1, 4}</code>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Quantile.html">Quantile</a> */
public class Quantile implements ScalarUnaryOperator {
  /** @param tensor non-empty
   * @return
   * @throws Exception if input is a scalar */
  public static ScalarUnaryOperator of(Tensor tensor) {
    if (0 < tensor.length())
      return new Quantile(Sort.of(VectorQ.require(tensor)));
    throw TensorRuntimeException.of(tensor);
  }

  /** @param sorted vector non-empty
   * @return
   * @throws Exception if given sorted vector is not ordered
   * @see OrderedQ */
  public static ScalarUnaryOperator ofSorted(Tensor sorted) {
    return new Quantile(OrderedQ.require(sorted));
  }

  /***************************************************/
  private final Tensor tensor;
  private final Scalar length;

  private Quantile(Tensor tensor) {
    this.tensor = tensor;
    length = RealScalar.of(Integers.requirePositive(tensor.length()));
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return tensor.Get(scalar.equals(RealScalar.ZERO) //
        ? 0
        : Scalars.intValueExact(Ceiling.FUNCTION.apply(scalar.multiply(length))) - 1);
  }
}
