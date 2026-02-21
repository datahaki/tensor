// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.ArcCos;
import ch.alpine.tensor.sca.tri.Cos;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArcSinDistribution.html">ArcSinDistribution</a> */
public class ArcSinDistribution extends AbstractContinuousDistribution implements Serializable {
  public static final Distribution INSTANCE = new ArcSinDistribution();

  private ArcSinDistribution() {
  }

  @Override
  public Scalar at(Scalar x) {
    Scalar f1 = x.one().subtract(x);
    Scalar f2 = x.one().add(x);
    return Sqrt.FUNCTION.apply(f1.multiply(f2)).multiply(Pi.VALUE).reciprocal();
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    return RealScalar.ONE.subtract(ArcCos.FUNCTION.apply(x).divide(Pi.VALUE));
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    return Cos.FUNCTION.apply(Pi.VALUE.multiply(p)).negate();
  }

  @Override
  public Scalar mean() {
    return RealScalar.ZERO;
  }

  @Override
  public Scalar variance() {
    return Rational.HALF;
  }

  @Override
  public Clip support() {
    return Clips.absoluteOne();
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("ArcSinDistribution");
  }
}
