// code by jph
package ch.ethz.idsc.tensor.num;

/** @see BinaryPower */
public interface GroupInterface<T> {
  /** @return value when exponent equals zero */
  T identity();

  /** @param object to invert when the given exponent is negative
   * @return */
  T invert(T object);

  /** @param factor1
   * @param factor2
   * @return product factor1 * factor2 */
  T combine(T factor1, T factor2);
}
