// code by jph, gjoel
package ch.alpine.tensor.pdf;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HistogramDistributionTest extends TestCase {
  public void testPdf() {
    Distribution distribution = //
        HistogramDistribution.of(Tensors.vector(-3, -3, -2, -2, 10), RealScalar.of(2));
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(RealScalar.of(-3)), RationalScalar.of(1, 5));
    assertEquals(pdf.at(RealScalar.of(-4)), RationalScalar.of(1, 5));
    assertEquals(pdf.at(RealScalar.of(-4.1)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(11)), RationalScalar.of(1, 10));
    Clip c1 = Clips.interval(-4, 0);
    Clip c2 = Clips.interval(10, 12);
    Set<Scalar> set = new HashSet<>();
    for (int count = 0; count < 100; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertTrue(c1.isInside(scalar) || c2.isInside(scalar));
      set.add(scalar);
    }
    assertTrue(90 < set.size());
  }

  public void testFreedman() {
    Tensor samples = Tensors.vector(-4, -3, -3, -2, -2, 10);
    Distribution distribution = HistogramDistribution.of(samples, BinningMethod.IQR);
    PDF pdf = PDF.of(distribution);
    assertTrue(Scalars.nonZero(pdf.at(RealScalar.of(-3))));
    assertTrue(Scalars.lessThan(RealScalar.ONE, BinningMethod.IQR.apply(samples)));
  }

  public void testFreedmanMin() {
    Distribution distribution = HistogramDistribution.of(Tensors.vector(3, 4));
    assertTrue(distribution.toString().startsWith("HistogramDistribution"));
    AssertFail.of(() -> HistogramDistribution.of(Tensors.vector(3, 3)));
  }

  public void testScott() {
    Tensor samples = Tensors.vector(-4, -3, -3, -2, -2, 10);
    Distribution distribution = HistogramDistribution.of(samples, BinningMethod.VARIANCE);
    PDF pdf = PDF.of(distribution);
    assertTrue(Scalars.nonZero(pdf.at(RealScalar.of(-3))));
    assertTrue(Scalars.lessThan(RealScalar.ONE, BinningMethod.VARIANCE.apply(samples)));
  }

  public void testQuantity() {
    Tensor vector = QuantityTensor.of(Tensors.vector(1, 1.7, 2, 3, 3.9, 4, 4.1), "m");
    Scalar width = Quantity.of(0.7, "m");
    Distribution distribution = //
        HistogramDistribution.of(vector, width);
    assertTrue(RandomVariate.of(distribution) instanceof Quantity);
    PDF pdf = PDF.of(distribution);
    assertEquals(pdf.at(Quantity.of(0, "m")), RealScalar.ZERO.divide(width));
    assertEquals(pdf.at(Quantity.of(1.2, "m")), RationalScalar.of(1, 7).divide(width));
    assertEquals(pdf.at(Quantity.of(4.15, "m")), RationalScalar.of(3, 7).divide(width));
    Clip clip = Clips.interval(Quantity.of(0.7, "m"), Quantity.of(4.2, "m"));
    Set<Scalar> set = new HashSet<>();
    for (int count = 0; count < 100; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertTrue(clip.isInside(scalar));
      set.add(scalar);
    }
    assertTrue(90 < set.size());
  }

  public void testMean() {
    Tensor vector = QuantityTensor.of(Tensors.vector(1, 2, 3), "m");
    Distribution distribution = //
        HistogramDistribution.of(vector, Quantity.of(1, "m"));
    Scalar mean = Expectation.mean(distribution);
    assertEquals(mean, Quantity.of(2.5, "m"));
  }

  public void testVariance() {
    Tensor vector = QuantityTensor.of(Tensors.vector(1, 2, 3), "m");
    Distribution distribution = HistogramDistribution.of(vector, Quantity.of(1, "m"));
    assertEquals( //
        Expectation.variance(distribution), //
        Quantity.of( //
            Expectation.variance(UniformDistribution.of(0, 3)), "m^2"));
  }

  public void testVariance1() {
    assertEquals( //
        Expectation.variance(HistogramDistribution.of(Tensors.vector(0.5), RealScalar.of(1))), //
        RationalScalar.of(1, 12));
    assertEquals( //
        Expectation.variance(HistogramDistribution.of(Tensors.vector(0.5), RealScalar.of(2))), //
        Expectation.variance(UniformDistribution.of(0, 2)));
  }

  public void testVariance2() {
    assertEquals( //
        Expectation.variance(HistogramDistribution.of(Tensors.vector(0.5, 1.5), RealScalar.of(1))), //
        Expectation.variance(UniformDistribution.of(0, 2)));
    assertEquals( //
        Expectation.variance(HistogramDistribution.of(Tensors.vector(0.5, 2.5), RealScalar.of(2))), //
        Expectation.variance(UniformDistribution.of(0, 4)));
  }

  public void testVarianceIr1() {
    assertEquals(Expectation.variance(HistogramDistribution.of(Tensors.vector(0.5, 1.5, 1.5), RealScalar.of(1))), RationalScalar.of(11, 36));
    assertEquals(Expectation.variance(HistogramDistribution.of(Tensors.vector(2.5, 1.5, 1.5), RealScalar.of(1))), RationalScalar.of(11, 36));
  }

  public void testVarianceIr2() {
    assertEquals(Expectation.variance(HistogramDistribution.of(Tensors.vector(4.5, 4.5, 2.5), RealScalar.of(2))), RationalScalar.of(11, 9));
    assertEquals(Expectation.variance(HistogramDistribution.of(Tensors.vector(2.5, 1.5, 1.5), RealScalar.of(2))), RationalScalar.of(11, 9));
  }

  public void testVarianceIr3() {
    assertEquals(Expectation.variance(HistogramDistribution.of(Tensors.vector(4.5, 4.5, 2.5, 10.5), RealScalar.of(2))), RationalScalar.of(28, 3));
    assertEquals(Expectation.variance(HistogramDistribution.of(Tensors.vector(2.5, 1.5, 1.5, 10.5), RealScalar.of(2))), RationalScalar.of(52, 3));
  }

  public void testCDF() {
    Distribution distribution = HistogramDistribution.of(Tensors.vector(0.5, 1.5, 1.5, 2.2), RealScalar.of(1));
    CDF cdf = CDF.of(distribution);
    assertEquals(cdf.p_lessThan(RationalScalar.of(0, 1)), RationalScalar.of(0, 1));
    assertEquals(cdf.p_lessThan(RationalScalar.of(1, 1)), RationalScalar.of(1, 4));
    assertEquals(cdf.p_lessThan(RationalScalar.of(3, 2)), RationalScalar.of(1, 2));
    assertEquals(cdf.p_lessThan(RationalScalar.of(2, 1)), RationalScalar.of(3, 4));
    assertEquals(cdf.p_lessThan(RationalScalar.of(3, 1)), RationalScalar.of(1, 1));
  }

  public void testQuantityCDF() {
    Random random = new Random(3);
    Tensor samples = QuantityTensor.of(RandomVariate.of(UniformDistribution.of(100, 120), random, 100), "kg");
    HistogramDistribution distribution = (HistogramDistribution) HistogramDistribution.of(samples, Quantity.of(2, "kg"));
    Clip clip = distribution.support();
    assertEquals(clip, Clips.interval(Quantity.of(100, "kg"), Quantity.of(120, "kg")));
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Distribution ud = UniformDistribution.of(clip);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(ud);
      Scalar p = cdf.p_lessEquals(x);
      Scalar q = inverseCDF.quantile(p);
      Tolerance.CHOP.requireClose(x, q);
    }
  }

  public void testInverseCDF() {
    Distribution distribution = HistogramDistribution.of(Tensors.vector(0.5, 1.5, 1.5, 2.2), RealScalar.of(1));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    {
      Scalar x = inverseCDF.quantile(RealScalar.ZERO);
      ExactScalarQ.require(x);
      assertEquals(x, RealScalar.ZERO);
    }
    {
      Scalar x = inverseCDF.quantile(RationalScalar.of(1, 2));
      ExactScalarQ.require(x);
      assertEquals(x, RationalScalar.of(3, 2));
    }
    {
      Scalar x = inverseCDF.quantile(RationalScalar.of(1, 4));
      ExactScalarQ.require(x);
      assertEquals(x, RationalScalar.of(1, 1));
    }
    {
      Scalar x = inverseCDF.quantile(RationalScalar.of(3, 4));
      ExactScalarQ.require(x);
      assertEquals(x, RationalScalar.of(2, 1));
    }
  }

  public void testInverseCDF2() {
    Tensor vector = QuantityTensor.of(Tensors.vector(1, 2, 3), "m");
    Distribution distribution = HistogramDistribution.of(vector, Quantity.of(2, "m"));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    {
      Scalar x = inverseCDF.quantile(RealScalar.ZERO);
      ExactScalarQ.require(x);
      assertEquals(x, Quantity.of(0, "m"));
    }
    {
      Scalar x = inverseCDF.quantile(RationalScalar.of(2, 3));
      ExactScalarQ.require(x);
      assertEquals(x, Quantity.of(RationalScalar.of(3, 1), "m"));
    }
    {
      Scalar x = inverseCDF.quantile(RationalScalar.of(1, 2));
      ExactScalarQ.require(x);
      assertEquals(x, Quantity.of(RationalScalar.of(5, 2), "m"));
    }
  }

  public void testInverseCDFOne() {
    Tensor vector = QuantityTensor.of(Tensors.vector(1, 2, 3), "m");
    Distribution distribution = HistogramDistribution.of(vector, Quantity.of(2, "m"));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(inverseCDF.quantile(RealScalar.ONE), Quantity.of(4, "m"));
  }

  public void testCDFInverseCDF() {
    Tensor samples = QuantityTensor.of(Tensors.vector(-6, -3.5, -1.3, 1, 2, 0, 3, 0, 3, 0), "m");
    HistogramDistribution distribution = (HistogramDistribution) HistogramDistribution.of(samples, Quantity.of(2, "m"));
    Clip clip = distribution.support();
    assertEquals(distribution.support(), Clips.interval(Quantity.of(-6, "m"), Quantity.of(4, "m")));
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Distribution ud = UniformDistribution.of(clip);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(ud);
      Scalar p = cdf.p_lessEquals(x);
      Scalar q = inverseCDF.quantile(p);
      Tolerance.CHOP.requireClose(x, q);
    }
  }

  public void testFailEmpty() {
    AssertFail.of(() -> HistogramDistribution.of(Tensors.empty(), RealScalar.of(2)));
  }

  public void testFailWidth() {
    AssertFail.of(() -> HistogramDistribution.of(Tensors.vector(1, 2, 3), RealScalar.ZERO));
    AssertFail.of(() -> HistogramDistribution.of(Tensors.vector(1, 2, 3), RealScalar.of(-2)));
  }
}
