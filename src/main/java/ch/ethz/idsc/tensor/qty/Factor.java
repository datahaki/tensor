// code by jph
package ch.ethz.idsc.tensor.qty;

import ch.ethz.idsc.tensor.Scalar;

/* package */ interface Factor {
  /** @param quantity
   * @return */
  Scalar times(Quantity quantity);

  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/UnitDimensions.html">UnitDimensions</a>
   * 
   * @param unit
   * @return */
  Unit dimensions(Unit unit);
}
