// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;

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
