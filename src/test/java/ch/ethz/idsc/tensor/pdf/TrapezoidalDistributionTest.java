// code by clruch
package ch.ethz.idsc.tensor.pdf;

import java.io.IOException;
import java.util.Random;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TrapezoidalDistributionTest extends TestCase {
  final Random random = new Random();

  public void testPositive() {
    Scalar a = RationalScalar.of(random.nextInt(100), 1);
    Scalar b = a.add(RealScalar.of(random.nextDouble() * 10));
    Scalar c = b.add(RealScalar.of(random.nextDouble() * 10));
    Scalar d = c.add(RealScalar.of(random.nextDouble() * 10));
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertTrue(Scalars.lessEquals(RealScalar.ZERO, scalar));
    }
  }

  public void testPDF() throws ClassNotFoundException, IOException {
    Scalar a = RationalScalar.of(1, 1);
    Scalar b = RationalScalar.of(2, 1);
    Scalar c = RationalScalar.of(3, 1);
    Scalar d = RationalScalar.of(4, 1);
    Distribution distribution = Serialization.copy(TrapezoidalDistribution.of(a, b, c, d));
    {
      Scalar actual = PDF.of(distribution).at(RealScalar.of(3));
      Scalar expected = RealScalar.of(2).divide(d.add(c).subtract(a).subtract(b));
      assertEquals(expected, actual);
    }
    {
      assertEquals(PDF.of(distribution).at(RealScalar.of(-3)), RealScalar.ZERO);
      assertEquals(PDF.of(distribution).at(RealScalar.of(13)), RealScalar.ZERO);
    }
  }

  public void testCDFPositive() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Scalar c = RealScalar.of(3);
    Scalar d = RealScalar.of(4);
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessEquals(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(cdf.p_lessEquals(RealScalar.of(1.5)), RationalScalar.of(1, 16));
    assertEquals(cdf.p_lessEquals(RealScalar.of(+4)), RealScalar.ONE);
  }

  public void testMean() {
    Scalar a = RandomVariate.of(DiscreteUniformDistribution.of(0, 100));
    Distribution paramDist = UniformDistribution.of(0, 10);
    Scalar b = a.add(RandomVariate.of(paramDist));
    Scalar c = b.add(RandomVariate.of(paramDist));
    Scalar d = c.add(RandomVariate.of(paramDist));
    Distribution distribution = TrapezoidalDistribution.of(a, b, c, d);
    Tensor all = RandomVariate.of(distribution, 3000);
    Scalar meanCalc = Mean.of(distribution);
    Scalar meanSamples = (Scalar) Mean.of(all);
    Scalar diff = Abs.between(meanCalc, meanSamples);
    assertTrue(Scalars.lessEquals(diff, RealScalar.of(0.5)));
  }

  public void testVarianceFail() {
    TrapezoidalDistribution distribution = (TrapezoidalDistribution) TrapezoidalDistribution.of(1, 2, 3, 4);
    AssertFail.of(() -> distribution.variance());
  }

  public void testQuantity() {
    Distribution distribution = //
        TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(5, "m"));
    Scalar mean = Mean.of(distribution);
    assertEquals(mean, Scalars.fromString("14/5[m]"));
    ExactScalarQ.require(mean);
    PDF pdf = PDF.of(distribution);
    {
      Scalar density = pdf.at(Quantity.of(3, "m"));
      assertEquals(density, Scalars.fromString("2/5[m^-1]"));
    }
    CDF cdf = CDF.of(distribution);
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(4, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RationalScalar.of(9, 10));
    }
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(6, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RealScalar.ONE);
    }
    Scalar random = RandomVariate.of(distribution);
    Scalar apply = QuantityMagnitude.SI().in("km").apply(random);
    assertTrue(apply instanceof RealScalar);
  }

  public void testQuantity2() {
    Distribution distribution = //
        TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(4, "m"));
    Scalar mean = Mean.of(distribution);
    assertEquals(mean, Scalars.fromString("5/2[m]"));
    ExactScalarQ.require(mean);
    PDF pdf = PDF.of(distribution);
    {
      Scalar density = pdf.at(Scalars.fromString("3/2[m]"));
      ExactScalarQ.require(density);
      assertEquals(density, Scalars.fromString("1/4[m^-1]"));
    }
    {
      Scalar density = pdf.at(Quantity.of(2.5, "m"));
      ExactScalarQ.require(density);
      assertEquals(density, Scalars.fromString("1/2[m^-1]"));
    }
    {
      Scalar density = pdf.at(Scalars.fromString("7/2[m]"));
      ExactScalarQ.require(density);
      assertEquals(density, Scalars.fromString("1/4[m^-1]"));
    }
    CDF cdf = CDF.of(distribution);
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(4, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RationalScalar.of(10, 10));
    }
    {
      Scalar prob = cdf.p_lessEquals(Quantity.of(6, "m"));
      ExactScalarQ.require(prob);
      assertEquals(prob, RealScalar.ONE);
    }
    Scalar random = RandomVariate.of(distribution);
    Scalar apply = QuantityMagnitude.SI().in("km").apply(random);
    assertTrue(apply instanceof RealScalar);
    assertTrue(distribution.toString().startsWith("TrapezoidalDistribution["));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(-0.1)));
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(+1.1)));
  }

  public void testExactFail() {
    TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(3, "m"));
    TrapezoidalDistribution.of(Quantity.of(2, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(3, "m"));
    AssertFail.of(() -> TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(1, "m"), Quantity.of(1, "m"), Quantity.of(1, "m")));
    AssertFail.of(() -> TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(3, "m"), Quantity.of(1, "m")));
    // AssertFail.of(() ->
    TrapezoidalDistribution.of(Quantity.of(1, "m"), Quantity.of(2, "m"), Quantity.of(2, "m"), Quantity.of(5, "m"));
  }

  public void testNumericFail() {
    TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(3., "m"));
    TrapezoidalDistribution.of(Quantity.of(2., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(3., "m"));
    AssertFail.of(() -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(1., "m"), Quantity.of(1., "m"), Quantity.of(1., "m")));
    AssertFail.of(() -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(3., "m"), Quantity.of(1., "m")));
    // AssertFail.of(() ->
    TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(2., "m"), Quantity.of(2., "m"), Quantity.of(5., "m"));
  }

  public void testCenterFail() {
    AssertFail.of(() -> TrapezoidalDistribution.of(Quantity.of(1., "m"), Quantity.of(3., "m"), Quantity.of(2., "m"), Quantity.of(9., "m")));
  }
}
