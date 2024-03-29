// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
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
}
