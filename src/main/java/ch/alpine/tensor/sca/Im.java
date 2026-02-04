// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Im projects a given scalar to its imaginary part.
 * The scalar type is required to implement {@link ComplexEmbedding}
 * in order for the operation to succeed.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Im.html">Im</a> */
public enum Im implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ComplexEmbedding complexEmbedding)
      return complexEmbedding.imag();
    throw new Throw(scalar);
  }

  /** @param tensor
   * @return if all entries of given tensor have imaginary part equals 0 */
  public static boolean allZero(Tensor tensor) {
    return Flatten.scalars(tensor).map(Im.FUNCTION).allMatch(Scalars::isZero);
  }
}
