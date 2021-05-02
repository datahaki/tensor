// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;

/** interface defines the embedding of a {@link Scalar} in the complex plane
 * 
 * <p>the interface is allows to extract the imaginary part of general instances
 * of {@link Scalar}s, such as {@link Quantity}.
 * 
 * @see Real
 * @see Imag */
public interface ComplexEmbedding {
  /** @return real part */
  Scalar real();

  /** @return imaginary part */
  Scalar imag();
}
