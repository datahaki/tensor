// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.mat.ex.MatrixPower;

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

  /** @param factor1
   * @param factor2
   * @return product factor1 * factor2 */
  T combine(T factor1, T factor2);
}
