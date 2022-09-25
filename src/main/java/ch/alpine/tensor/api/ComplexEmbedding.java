// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/** interface defines the embedding of a {@link Scalar} in the complex plane
 * 
 * <p>the interface is allows to extract the imaginary part of general instances
 * of {@link Scalar}s, such as {@link Quantity}.
 * 
 * @see Re
 * @see Im */
public interface ComplexEmbedding {
  /** @return real part */
  Scalar real();

  /** @return imaginary part */
  Scalar imag();
}
