// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.OrderedQ;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.sca.Ceiling;

/** Quantile of the tensor library only operates on vectors. The return type of
 * the quantile function is {@link Scalar}.
 * 
 * <p>Mathematica::Quantile also operates (trivially) on matrices.
 * 
 * <p>Quantile does not average as {@link Median}:
 * <code>Quantile[{1, 2}].apply(0.5) == 1</code>,
 * <code>Median[{1, 2}] == 3/2</code>
 * 
 * <p>for vector input, the implementation is compliant to Mathematica.
 * Mathematica::Quantile also allows matrices as input.
 * 
 * <p>Function arguments are required to be in the interval [0, 1].
 * 
 * <p>Example:
 * <code>Quantile[{0, 1, 2, 3, 4}] applied to {0, 1/5, 2/5, 1} == {0, 0, 1, 4}</code>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Quantile.html">Quantile</a> */
public class Quantile implements ScalarUnaryOperator {
  private static final long serialVersionUID = -4076649064391350185L;

  /** @param vector non-empty
   * @return
   * @throws Exception if input is a scalar */
  public static ScalarUnaryOperator of(Tensor vector) {
    return new Quantile(Sort.of(VectorQ.require(vector)));
  }

  /** @param vector non-empty with entries sorted
   * @return
   * @throws Exception if given sorted vector is not ordered
   * @see OrderedQ */
  public static ScalarUnaryOperator ofSorted(Tensor vector) {
    return new Quantile(OrderedQ.require(VectorQ.require(vector)));
  }

  /** @param distribution
   * @return function p -> InverseCDF[distribution, p]
   * @see InverseCDF */
  public static ScalarUnaryOperator of(Distribution distribution) {
    InverseCDF inverseCDF = (InverseCDF) distribution;
    return inverseCDF::quantile;
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
        : Ceiling.intValueExact(scalar.multiply(length)) - 1);
  }
}
