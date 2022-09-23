// code by jph
package ch.alpine.tensor.sca.ply;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.bes.BesselI;
import ch.alpine.tensor.sca.exp.Exp;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LaguerreL.html">LaguerreL</a> */
public enum LaguerreL {
  ;
  /** @param n
   * @param x
   * @return */
  public static Scalar of(Scalar n, Scalar x) {
    if (n.equals(RationalScalar.HALF)) {
      Scalar x2 = x.multiply(RationalScalar.HALF);
      Scalar s1 = x.multiply(BesselI._1(x2));
      Scalar s2 = x.subtract(RealScalar.ONE).multiply(BesselI._0(x2));
      return s1.subtract(s2).multiply(Exp.FUNCTION.apply(x2));
    }
    throw new Throw(n, x);
  }
}
