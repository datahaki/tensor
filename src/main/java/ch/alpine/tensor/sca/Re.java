// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Re projects a given scalar to its real part.
 * The scalar type is required to implement {@link ComplexEmbedding}
 * in order for the operation to succeed.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Re.html">Re</a> */
public enum Re implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ComplexEmbedding complexEmbedding)
      return complexEmbedding.real();
    throw new Throw(scalar);
  }
}
