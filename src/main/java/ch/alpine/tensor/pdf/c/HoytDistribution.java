// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.bes.BesselI;
import ch.alpine.tensor.sca.exp.Exp;

/** CDF requires MarcumQ
 * Mean requires EllipticE */
public class HoytDistribution implements Distribution, //
    PDF, Serializable {
  public static Distribution of(Scalar q, Scalar w) {
    Sign.requirePositive(q);
    if (Scalars.lessThan(RealScalar.ZERO, w))
      return new HoytDistribution(Clips.unit().requireInside(q), w);
    throw new Throw(q, w);
  }

  public static Distribution of(Number q, Number w) {
    return of(RealScalar.of(q), RealScalar.of(w));
  }

  // ---
  private final Scalar q;
  private final Scalar w;
  private final Scalar q2;

  private HoytDistribution(Scalar q, Scalar w) {
    this.q = q;
    this.w = w;
    q2 = q.multiply(q);
  }

  @Override
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar _1_q2 = RealScalar.ONE.add(q2);
      Scalar _1qx = _1_q2.multiply(x);
      Scalar den = q2.multiply(w).multiply(RealScalar.of(-4));
      Scalar factor = Exp.FUNCTION.apply(_1qx.multiply(_1qx).divide(den));
      if (Scalars.nonZero(factor)) {
        Scalar y = Times.of(RealScalar.ONE.subtract(q2.multiply(q2)), x, x).divide(den);
        return Times.of(factor, _1_q2, x, BesselI._0(y)).divide(q.multiply(w));
      }
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("HoytDistribution", q, w);
  }
}
