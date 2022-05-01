// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Median;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;

public class ExponentialDistributionTest {
  @Test
  public void testPositive() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ExponentialDistribution.of(RealScalar.ONE));
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertTrue(Scalars.lessEquals(RealScalar.ZERO, scalar));
    }
  }

  @Test
  public void testPDF() {
    Distribution distribution = ExponentialDistribution.of(2);
    {
      Scalar actual = PDF.of(distribution).at(RealScalar.of(3));
      Scalar expected = RealScalar.of(2).divide(Exp.of(RealScalar.of(6)));
      assertEquals(expected, actual);
    }
    {
      Scalar actual = PDF.of(distribution).at(RealScalar.of(-3));
      assertEquals(actual, RealScalar.ZERO);
    }
  }

  @Test
  public void testCDFPositive() {
    Distribution distribution = ExponentialDistribution.of(RealScalar.of(2));
    CDF cdf = CDF.of(distribution);
    Scalar actual = cdf.p_lessEquals(RealScalar.of(3));
    Scalar expected = RealScalar.ONE.subtract(Exp.of(RealScalar.of(6)).reciprocal());
    assertEquals(expected, actual);
  }

  @Test
  public void testCDFNegative() {
    Distribution distribution = ExponentialDistribution.of(RealScalar.ONE);
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(cdf.p_lessThan(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(RealScalar.of(0)), RealScalar.ZERO);
  }

  @Test
  public void testMean() {
    Scalar lambda = RealScalar.of(2);
    Distribution distribution = ExponentialDistribution.of(lambda);
    Tensor all = Tensors.empty();
    for (int c = 0; c < 2000; ++c) {
      Scalar s = RandomVariate.of(distribution);
      all.append(s);
    }
    Scalar mean = lambda.reciprocal();
    assertEquals(Expectation.mean(distribution), mean);
    Scalar diff = Abs.between(Mean.ofVector(all), mean);
    assertTrue(Scalars.lessThan(diff, RealScalar.of(0.05)));
    Tolerance.CHOP.requireClose(Median.of(distribution), Log.FUNCTION.apply(RealScalar.of(2)).divide(lambda));
  }

  @Test
  public void testFailL() {
    assertThrows(TensorRuntimeException.class, () -> ExponentialDistribution.of(RealScalar.ZERO));
    assertThrows(TensorRuntimeException.class, () -> ExponentialDistribution.of(RealScalar.of(-0.1)));
  }

  @Test
  public void testNextUp() {
    double zero = 0;
    double nonzero = Math.nextUp(zero);
    double log = Math.log(nonzero);
    assertTrue(-2000 < log);
  }

  private static void _checkCorner(Scalar lambda) {
    ExponentialDistribution exponentialDistribution = (ExponentialDistribution) ExponentialDistribution.of(lambda);
    Scalar from0 = exponentialDistribution.randomVariate(0);
    assertTrue(FiniteScalarQ.of(from0));
    assertTrue(Scalars.lessThan(RealScalar.ZERO, from0));
    double max = Math.nextDown(1.0);
    Scalar from1 = exponentialDistribution.randomVariate(max);
    assertTrue(Scalars.lessEquals(RealScalar.ZERO, from1));
    assertFalse(Scalars.lessThan(RealScalar.ZERO, exponentialDistribution.randomVariate(1)));
  }

  @Test
  public void testCornerCase() {
    _checkCorner(RealScalar.of(0.00001));
    _checkCorner(RealScalar.of(0.1));
    _checkCorner(RealScalar.of(1));
    _checkCorner(RealScalar.of(2));
    _checkCorner(RealScalar.of(700));
  }

  @Test
  public void testQuantity() {
    Distribution distribution = ExponentialDistribution.of(Quantity.of(3, "m"));
    Scalar rand = RandomVariate.of(distribution);
    assertInstanceOf(Quantity.class, rand);
    UnitConvert.SI().to(Unit.of("mi^-1")).apply(rand);
    assertInstanceOf(Quantity.class, Expectation.mean(distribution));
    Scalar var = Expectation.variance(distribution);
    assertInstanceOf(Quantity.class, var);
  }

  @Test
  public void testInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(ExponentialDistribution.of(Quantity.of(3, "")));
    Scalar x0 = inverseCDF.quantile(RealScalar.of(0.0));
    Scalar x1 = inverseCDF.quantile(RealScalar.of(0.2));
    Scalar x2 = inverseCDF.quantile(RealScalar.of(0.5));
    assertEquals(x0, RealScalar.ZERO);
    assertTrue(Scalars.lessThan(x1, x2));
  }

  @Test
  public void testInverseCDF_1() {
    InverseCDF inverseCDF = InverseCDF.of(ExponentialDistribution.of(Quantity.of(2.8, "")));
    assertEquals(inverseCDF.quantile(RealScalar.of(1.0)), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(inverseCDF.quantile(RealScalar.ONE), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  public void testCDFInverseCDF() {
    Distribution distribution = ExponentialDistribution.of(Quantity.of(2.8, "m*s^-1"));
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      Tolerance.CHOP.requireClose(x, q);
    }
  }

  @Test
  public void testToString() {
    Distribution distribution = ExponentialDistribution.of(Quantity.of(3, "m"));
    String string = distribution.toString();
    assertEquals(string, "ExponentialDistribution[3[m]]");
  }

  @Test
  public void testStandard() {
    assertEquals(Mean.of(ExponentialDistribution.standard()), RealScalar.ONE);
    assertEquals(Variance.of(ExponentialDistribution.standard()), RealScalar.ONE);
  }

  @Test
  public void testMarkov() {
    Random random = new Random();
    Distribution distribution = ExponentialDistribution.of(0.1 + 2 * random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
    TestMarkovChebyshev.chebyshev(distribution);
  }

  @Test
  public void testFailInverseCDF() {
    InverseCDF inverseCDF = InverseCDF.of(ExponentialDistribution.of(Quantity.of(3, "")));
    assertThrows(TensorRuntimeException.class, () -> inverseCDF.quantile(RealScalar.of(1.1)));
  }

  @Test
  public void testQuantityPDF() {
    Distribution distribution = ExponentialDistribution.of(Quantity.of(3, "m"));
    {
      Scalar prob = PDF.of(distribution).at(Quantity.of(2, "m^-1"));
      assertTrue(Sign.isPositive(prob));
      assertTrue(prob instanceof Quantity);
    }
    {
      Scalar prob = PDF.of(distribution).at(Quantity.of(-2, "m^-1"));
      assertEquals(prob.toString(), "0[m]");
    }
  }

  @Test
  public void testQuantityCDF() {
    Distribution distribution = ExponentialDistribution.of(Quantity.of(3, "m"));
    Scalar scalar = CentralMoment.of(distribution, 5);
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Scalars.fromString("44/243[m^-5]"));
    {
      Scalar prob = CDF.of(distribution).p_lessThan(Quantity.of(2, "m^-1"));
      assertTrue(Sign.isPositive(prob));
      assertInstanceOf(RealScalar.class, prob);
    }
    {
      Scalar prob = CDF.of(distribution).p_lessEquals(Quantity.of(-2, "m^-1"));
      assertEquals(prob.toString(), "0");
    }
  }
}
