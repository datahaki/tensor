// code by jph
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Reference:
 * "Incomplete Gamma Function" in NR, 2007
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/InverseErfc.html">InverseErfc</a> */
public enum InverseErfc implements ScalarUnaryOperator {
  FUNCTION;

  private static final Clip CLIP = Clips.interval(0, 2.0);
  private static final Scalar TWO = RealScalar.of(2.0);
  private static final Scalar FAC = RealScalar.of(1.12837916709551257);

  @Override
  public Scalar apply(Scalar p) {
    if (CLIP.min().equals(p))
      return DoubleScalar.POSITIVE_INFINITY;
    if (CLIP.max().equals(p))
      return DoubleScalar.NEGATIVE_INFINITY;
    CLIP.requireInside(p);
    boolean lessThan1 = Scalars.lessThan(p, RealScalar.ONE);
    Scalar pp = lessThan1 ? p : TWO.subtract(p);
    double t = Sqrt.FUNCTION.apply(TWO.negate().multiply(Log.FUNCTION.apply(pp.divide(TWO)))).number().doubleValue();
    Scalar x = RealScalar.of(-0.70711 * ((2.30753 + t * 0.27061) / (1. + t * (0.99229 + t * 0.04481)) - t));
    for (int j = 0; j < 2; ++j) {
      Scalar err = Erfc.FUNCTION.apply(x).subtract(pp);
      Scalar den = FAC.multiply(Exp.FUNCTION.apply(x.multiply(x).negate())).subtract(x.multiply(err));
      x = x.add(err.divide(den));
    }
    return lessThan1 ? x : x.negate();
  }
}
