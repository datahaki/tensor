// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/** interface may be implemented by {@link Scalar} to support the computation of exponents.
 * Supported types include {@link RealScalar}, {@link ComplexScalar}. */
@FunctionalInterface
public interface PowerInterface {
  /** @param exponent
   * @return this scalar to the power of exponent */
  Scalar power(Scalar exponent);
}
