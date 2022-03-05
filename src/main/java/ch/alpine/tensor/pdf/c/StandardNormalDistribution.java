// code by jph
package ch.alpine.tensor.pdf.c;

import java.util.Random;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.KurtosisInterface;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.sca.erf.Erfc;
import ch.alpine.tensor.sca.erf.InverseErfc;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Sqrt;

/* package */ enum StandardNormalDistribution implements UnivariateDistribution, KurtosisInterface {
  INSTANCE;

  private static final Scalar DEN = Sqrt.FUNCTION.apply(Pi.TWO);
  private static final Scalar HALF = RealScalar.of(0.5);
  private static final Scalar FACTOR = Sqrt.FUNCTION.apply(RealScalar.TWO).negate();

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Exp.FUNCTION.apply(x.multiply(x).multiply(HALF).negate()).divide(DEN);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    // 1/2 Erfc[-(x/Sqrt[2])]
    return Erfc.FUNCTION.apply(x.divide(FACTOR)).multiply(HALF);
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return DoubleScalar.of(random.nextGaussian());
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return InverseErfc.FUNCTION.apply(p.add(p)).multiply(FACTOR);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.ZERO;
  }

  @Override // from MeanInterface
  public Scalar variance() {
    return RealScalar.ONE;
  }

  @Override // from KurtosisInterface
  public Scalar kurtosis() {
    return RealScalar.of(3);
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
