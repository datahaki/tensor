// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.sca.exp.Exp;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Pochhammer.html">Pochhammer</a> */
public enum Pochhammer {
  ;
  /** @param a
   * @param n
   * @return Gamma[a + n] / Gamma[a] */
  public static Scalar of(Scalar a, Scalar n) {
    return ExactScalarQ.of(a) && ExactScalarQ.of(n) //
        ? Gamma.FUNCTION.apply(a.add(n)).divide(Gamma.FUNCTION.apply(a))
        : Exp.FUNCTION.apply(LogGamma.FUNCTION.apply(a.add(n)).subtract(LogGamma.FUNCTION.apply(a)));
  }

  /** @param a
   * @param n
   * @return Gamma[a + n] / Gamma[a] */
  public static Scalar of(Number a, Number n) {
    return of(RealScalar.of(a), RealScalar.of(n));
  }
}
