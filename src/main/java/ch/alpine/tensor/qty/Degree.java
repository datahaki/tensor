// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Degree.html">Degree</a> */
public enum Degree {
  ;
  private static final Unit DEGREE = Unit.of("deg");

  /** the equivalent conversion formula in Mathematica is
   * QuantityMagnitude[Quantity[1, "Degrees"], "Radians"]
   * 
   * @param degree
   * @return radian == degree / 180 * PI */
  public static Scalar of(Number degree) {
    return of(RealScalar.of(degree));
  }

  /** @param degree
   * @return radian == degree / 180 * PI */
  public static Scalar of(Scalar degree) {
    return UnitSystem.SI().apply(Quantity.of(degree, DEGREE));
  }
}
