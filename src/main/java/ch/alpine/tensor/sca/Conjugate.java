// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ConjugateInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.ConjugateTranspose;

/** Conjugate maps a given scalar to its complex conjugate.
 * The scalar type is required to implement {@link ConjugateInterface}
 * in order for the operation to succeed.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Conjugate.html">Conjugate</a>
 * 
 * @see ConjugateInterface
 * @see ConjugateTranspose */
public enum Conjugate implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ConjugateInterface conjugateInterface)
      return conjugateInterface.conjugate();
    throw Throw.of(scalar);
  }

  /** @param tensor
   * @return tensor with all entries conjugated */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
