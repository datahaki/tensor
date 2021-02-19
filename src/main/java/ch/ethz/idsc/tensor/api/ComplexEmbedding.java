// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

/** interface defines the embedding of a {@link Scalar} in the complex plane
 * 
 * @see Real
 * @see Imag */
public interface ComplexEmbedding {
  /** @return real part */
  Scalar real();

  /** @return imaginary part */
  Scalar imag();
}
