// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitDimensions.html">UnitDimensions</a> */
public interface UnitDimensions {
  ThreadLocal<UnitDimensions> THREAD_LOCAL = ThreadLocal.withInitial(() -> UnitDimensionsBase.SI);

  Scalar normalForm(Scalar a);

  /** Quantity::add delegates to {@link #plus(Scalar, Scalar)}, i.e. this function
   * when a.add(b) is invoked on Quantity a and Scalar b in case unit are not identical
   * 
   * function is symmetric plus(a, b) == plus(b, a)
   * 
   * @param a
   * @param b
   * @return */
  Scalar plus(Scalar a, Scalar b);
}
