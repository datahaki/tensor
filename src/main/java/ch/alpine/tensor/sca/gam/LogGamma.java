// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.tri.Sinc;

/** <pre>
 * LogGamma[x] == Log[Gamma[x]]
 * </pre>
 * 
 * <p>Careful: Generally not consistent with Mathematica for z with Re[z] < 0.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LogGamma.html">LogGamma</a>
 * 
 * @see Gamma
 * @see Beta */
public enum LogGamma implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar z) {
    if (Sign.isPositive(Re.FUNCTION.apply(z)))
      return LogGammaRestricted.FUNCTION.apply(z);
    Scalar zp = RealScalar.ONE.subtract(z);
    return Log.FUNCTION.apply(Sinc.FUNCTION.apply(Pi.VALUE.multiply(zp))).add(LogGammaRestricted.FUNCTION.apply(RealScalar.ONE.add(zp))).negate();
  }
}
