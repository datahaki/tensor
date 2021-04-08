// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.util.Random;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ enum StandardNormalDistribution implements ContinuousDistribution {
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

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
