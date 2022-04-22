// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.FiniteQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Chop;

public class ParetoDistributionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ParetoDistribution.of(RealScalar.of(2.3), RealScalar.of(1.8)));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(4.0)), RealScalar.of(0.16619372965993448));
    Tolerance.CHOP.requireClose(pdf.at(RealScalar.of(2.3)), RealScalar.of(0.7826086956521743));
    Tolerance.CHOP.requireZero(pdf.at(RealScalar.of(2.2)));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(RealScalar.of(4.0)), RealScalar.of(0.6306806007557013));
    Tolerance.CHOP.requireZero(cdf.p_lessEquals(RealScalar.of(2.3)));
    Tolerance.CHOP.requireZero(cdf.p_lessEquals(RealScalar.of(2.2)));
    TestMarkovChebyshev.markov(distribution);
  }

  @Test
  public void testMeanVariance() {
    Distribution distribution = ParetoDistribution.of(2.3, 7.8);
    Scalar mean = Mean.of(distribution);
    Scalar varc = Variance.of(distribution);
    Tensor tensor = RandomVariate.of(distribution, 1000);
    Scalar empiricalMean = Mean.ofVector(tensor);
    Scalar empiricalVarc = Variance.ofVector(tensor);
    Chop chop = Chop.below(0.3);
    chop.requireClose(mean, empiricalMean);
    chop.requireClose(varc, empiricalVarc);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Tolerance.CHOP.requireClose(inverseCDF.quantile(RealScalar.of(0.2)), RealScalar.of(2.366748969310483));
    assertEquals(inverseCDF.quantile(RealScalar.ZERO), RealScalar.of(2.3));
    assertEquals(inverseCDF.quantile(RealScalar.ONE), DoubleScalar.POSITIVE_INFINITY);
    assertTrue(distribution.toString().startsWith("ParetoDistribution["));
    TestMarkovChebyshev.markov(distribution);
  }

  @Test
  public void testMeanVarianceIndeterminate() {
    Distribution distribution = ParetoDistribution.of(2.3, 1);
    assertFalse(FiniteQ.of(Mean.of(distribution)));
    assertFalse(FiniteQ.of(Variance.of(distribution)));
  }

  @Test
  public void testMarkov() {
    Random random = new Random();
    Distribution distribution = ParetoDistribution.of(1.1 + random.nextDouble(), 1.1 + random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
  }

  @Test
  public void testNegativeFail() {
    assertThrows(TensorRuntimeException.class, () -> ParetoDistribution.of(RealScalar.of(2.3), RealScalar.of(0)));
    assertThrows(TensorRuntimeException.class, () -> ParetoDistribution.of(RealScalar.of(0), RealScalar.of(3)));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> ParetoDistribution.of(RealScalar.of(3.3), Quantity.of(2.3, "m")));
  }

  @Test
  public void testKQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> ParetoDistribution.of(Quantity.of(2.3, "m"), RealScalar.of(3.3)));
  }
}
