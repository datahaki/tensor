// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Conjugate;

/** @see Conjugate */
@FunctionalInterface
public interface ConjugateInterface {
  /** @return conjugate of this instance */
  Scalar conjugate();
}
