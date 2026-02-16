// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.StandardDeviationInterface;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** GammaDistribution[alpha, 1] */
/* package */ class Gamma1Distribution implements Distribution, //
    PDF, MeanInterface, VarianceInterface, StandardDeviationInterface, Serializable {
  final Scalar alpha;
  private final Scalar factor;

  public Gamma1Distribution(Scalar alpha) {
    this.alpha = alpha;
    factor = Gamma.FUNCTION.apply(alpha);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x))
      return Exp.FUNCTION.apply(x.negate()) //
          .multiply(Power.of(x, alpha.subtract(RealScalar.ONE))).divide(factor);
    return RealScalar.ZERO;
  }

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    if (Scalars.lessEquals(RealScalar.ONE, alpha)) {
      Scalar d = alpha.subtract(Rational.THIRD);
      Scalar c = Sqrt.FUNCTION.apply(RealScalar.of(9).multiply(d)).reciprocal();
      while (true) {
        Scalar Z = RandomVariate.of(NormalDistribution.standard(), randomGenerator);
        Scalar U = RandomVariate.of(UniformDistribution.unit(), randomGenerator);
        Scalar v = Power.of(c.multiply(Z).add(RealScalar.ONE), 3);
        if (Sign.isPositive(v)) {
          Scalar s0 = Z.multiply(Z).multiply(Rational.HALF);
          Scalar dv = d.multiply(v);
          Scalar res = s0.add(d).subtract(dv).add(Log.FUNCTION.apply(v).multiply(d));
          if (Scalars.lessThan(Log.FUNCTION.apply(U), res))
            return dv;
        }
      }
    }
    Scalar Y = RandomVariate.of(new Gamma1Distribution(alpha.add(RealScalar.ONE)), randomGenerator);
    Scalar U = RandomVariate.of(UniformDistribution.unit(), randomGenerator);
    return Y.multiply(Power.of(U, alpha.reciprocal()));
  }
  // CDF requires GammaRegularized

  @Override // from MeanInterface
  public Scalar mean() {
    return alpha;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return alpha;
  }

  @Override // from StandardDeviationInterface
  public Scalar standardDeviation() {
    return Sqrt.FUNCTION.apply(alpha);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("GammaDistribution", alpha, RealScalar.ONE);
  }
}
