// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Real projects a given scalar to its real part.
 * The scalar type is required to implement {@link ComplexEmbedding}
 * in order for the operation to succeed.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Re.html">Re</a> */
public enum Real implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ComplexEmbedding)
      return ((ComplexEmbedding) scalar).real();
    throw new Throw(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their real part */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
