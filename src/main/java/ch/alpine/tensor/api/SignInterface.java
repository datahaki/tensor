// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.Scalar;

/** classes that implement {@link SignInterface} should also implement
 * {@link AbsInterface} and achieve the identity
 * <pre>
 * scalar == Sign[scalar] * Abs[scalar]
 * </pre> */
@FunctionalInterface
public interface SignInterface {
  /** @return the "direction" of the scalar, or zero if scalar is zero */
  Scalar sign();
}
