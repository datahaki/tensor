// code by jph
package ch.alpine.tensor.red;

import java.util.Optional;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** Remark:
 * For unconditional division, invoke {@link Scalar#divide(Scalar)} directly.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Divide.html">Divide</a> */
public enum Divide {
  ;
  /** @param num numerator
   * @param den denominator
   * @return empty if den is zero */
  public static Optional<Scalar> nonZero(Scalar num, Scalar den) {
    return Scalars.isZero(den) //
        ? Optional.empty()
        : Optional.of(num.divide(den));
  }

  /** @param num
   * @param den
   * @return */
  public static Optional<Scalar> nonZero(Number num, Number den) {
    return nonZero(RealScalar.of(num), RealScalar.of(den));
  }
}
