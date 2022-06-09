// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Kurtosis;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;

class LogNormalDistributionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    LogNormalDistribution distribution = (LogNormalDistribution) Serialization.copy( //
        LogNormalDistribution.of(RationalScalar.HALF, RationalScalar.of(2, 3)));
    Tolerance.CHOP.requireClose(Mean.of(distribution), Exp.FUNCTION.apply(RationalScalar.of(13, 18)));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(2.372521698687904));
    {
      Scalar cdf = distribution.p_lessThan(RealScalar.of(0.7));
      Tolerance.CHOP.requireClose(cdf, RealScalar.of(0.09939397268253057)); // confirmed with Mathematica
    }
    {
      Scalar cdf = distribution.p_lessThan(RealScalar.of(-0.7));
      Tolerance.CHOP.requireClose(cdf, cdf.zero()); // confirmed with Mathematica
    }
    {
      Scalar cdf = distribution.p_lessEquals(RealScalar.of(0.7));
      Tolerance.CHOP.requireClose(cdf, RealScalar.of(0.09939397268253057)); // confirmed with Mathematica
    }
    {
      Scalar pdf = distribution.at(RealScalar.of(0.7));
      Tolerance.CHOP.requireClose(pdf, RealScalar.of(0.37440134735643077)); // confirmed with Mathematica
    }
    {
      Scalar pdf = distribution.at(RealScalar.of(-0.7));
      Tolerance.CHOP.requireClose(pdf, pdf.zero()); // confirmed with Mathematica
    }
    Scalar quantile = distribution.quantile(RealScalar.of(0.4));
    Tolerance.CHOP.requireClose(quantile, RealScalar.of(1.392501724505789));
    Scalar variate = RandomVariate.of(distribution);
    Sign.requirePositive(variate);
    assertEquals(distribution.toString(), distribution.getClass().getSimpleName() + "[1/2, 2/3]");
  }

  @Test
  public void testMean() {
    Distribution distribution = LogNormalDistribution.of(RationalScalar.of(4, 5), RationalScalar.of(2, 3));
    Scalar value = Mean.ofVector(RandomVariate.of(distribution, 200));
    Clips.interval(2.25, 3.5).requireInside(value);
    Tolerance.CHOP.requireClose(Mean.of(distribution), Exp.FUNCTION.apply(RationalScalar.of(46, 45)));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.of(4.323016391513655));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
  }

  @Test
  public void testCDFInverseCDF() {
    Distribution distribution = LogNormalDistribution.of(3, 0.2);
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      Chop._10.requireClose(x, q);
    }
  }

  @Test
  public void testKurtosis() {
    Tolerance.CHOP.requireClose(Kurtosis.of(LogNormalDistribution.of(3, 0.2)), RealScalar.of(3.678365777175438));
    Tolerance.CHOP.requireClose(Kurtosis.of(LogNormalDistribution.of(3, 1.2)), RealScalar.of(518.1684050407332));
  }

  @Test
  public void testMarkov() {
    Random random = new Random();
    Distribution distribution = LogNormalDistribution.of(random.nextDouble() - 0.5, 0.1 + random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
    TestMarkovChebyshev.chebyshev(distribution);
  }

  @Test
  public void testSigmaNonPositiveFail() {
    assertThrows(TensorRuntimeException.class, () -> LogNormalDistribution.of(RationalScalar.HALF, RealScalar.ZERO));
    assertThrows(TensorRuntimeException.class, () -> LogNormalDistribution.of(RationalScalar.HALF, RationalScalar.of(-2, 3)));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> LogNormalDistribution.of(Quantity.of(RationalScalar.HALF, "m"), RationalScalar.of(2, 3)));
    assertThrows(TensorRuntimeException.class, () -> LogNormalDistribution.of(RationalScalar.of(2, 3), Quantity.of(RationalScalar.HALF, "m")));
  }

  @Test
  public void testStandardString() {
    assertEquals(LogNormalDistribution.standard().toString(), "LogNormalDistribution[0, 1]");
  }
}
