// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

/* package */ enum StaticHelper {
  ;
  // 0.9999999999999999
  // .^....^....^....^.
  public static final Chop CDF_CHOP = Chop._14;

  /** @param p_equals
   * @param cumprob
   * @return
   * @see Exception */
  public static boolean isFinished(Scalar p_equals, Scalar cumprob) {
    if (cumprob.equals(RealScalar.ONE))
      return true;
    return p_equals.equals(RealScalar.ZERO) //
        && CDF_CHOP.isClose(cumprob, RealScalar.ONE);
  }
}
