// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitDimensions.html">UnitDimensions</a> */
public interface UnitDimensions {
  ThreadLocal<UnitDimensions> INSTANCE = ThreadLocal.withInitial(() -> UnitDimensionsBase.SI);

  Scalar normalForm(Scalar a);

  Scalar plus(Scalar a, Scalar b);
}
