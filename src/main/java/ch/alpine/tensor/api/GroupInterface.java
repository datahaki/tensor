// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.num.BinaryPower;

/** the tensor library uses the group properties to efficiently compute
 * powers of scalars and matrices where the exponent is an integer. The
 * algorithm for exponentiation is implemented in {@link BinaryPower}.
 * 
 * @see BinaryPower
 * @see MatrixPower */
public interface GroupInterface<T> {
  /** @return neutral element */
  T neutral(T element);

  /** @param element to invert when the given exponent is negative
   * @return inverse of given group element */
  T invert(T element);

  /** @param element1
   * @param element2
   * @return element1 group_operation element2 */
  T combine(T element1, T element2);
}
