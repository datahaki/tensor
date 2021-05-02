// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Conjugate;

/** @see Conjugate */
@FunctionalInterface
public interface ConjugateInterface {
  /** @return conjugate of this instance */
  Scalar conjugate();
}
