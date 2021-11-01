// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NormalDistributionTest extends TestCase {
  public void testExpectationMean() {
    Scalar mean = RationalScalar.of(3, 5);
    Distribution distribution = NormalDistribution.of(mean, RationalScalar.of(4, 9));
    assertEquals(Expectation.mean(distribution), mean);
  }

  public void testPDF() {
    Scalar mean = RationalScalar.of(3, 5);
    Scalar sigma = RationalScalar.of(4, 9);
    Distribution distribution = NormalDistribution.of(mean, sigma);
    PDF pdf = PDF.of(distribution);
    Scalar delta = RationalScalar.of(2, 3);
    // for delta with numerical precision, a small deviation is introduced
    assertEquals(pdf.at(mean.subtract(delta)), pdf.at(mean.add(delta)));
    // 0.8976201309032235253648786348523592040707
    assertTrue(pdf.at(mean).toString().startsWith("0.89762013090322"));
  }

  public void testFit() {
    Distribution distribution = BinomialDistribution.of(1000, RealScalar.of(1 / 3.));
    Distribution normal = NormalDistribution.fit(distribution);
    assertEquals(Expectation.mean(distribution), Expectation.mean(normal));
    Chop._12.requireClose(Expectation.variance(distribution), Expectation.variance(normal));
  }

  public void testCdf() {
    CDF cdf = (CDF) NormalDistribution.of(RealScalar.of(-10.2), RealScalar.of(2.3));
    Scalar x = RealScalar.of(-11);
    Scalar s = cdf.p_lessThan(x);
    assertEquals(s, cdf.p_lessEquals(x));
    assertTrue(s.toString().startsWith("0.363985"));
  }

  public void testQuantity() {
    Distribution distribution = NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m"));
    assertTrue(RandomVariate.of(distribution) instanceof Quantity);
    Scalar mean = Expectation.mean(distribution);
    assertTrue(mean instanceof Quantity);
    Scalar var = Expectation.variance(distribution);
    assertTrue(var instanceof Quantity);
    assertEquals(QuantityMagnitude.SI().in(Unit.of("m^2")).apply(var), RealScalar.of(4));
    {
      Scalar prob = PDF.of(distribution).at(mean);
      QuantityMagnitude.SI().in(Unit.of("in^-1")).apply(prob);
    }
    Chop._07.requireClose( //
        CDF.of(distribution).p_lessEquals(mean), //
        RationalScalar.of(1, 2));
  }

  public void testToString() {
    Distribution distribution = NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m"));
    String string = distribution.toString();
    assertEquals(string, "NormalDistribution[3[m], 2[m]]");
  }

  public void testCDFInverseCDF() {
    Distribution distribution = NormalDistribution.of(3, 0.2);
    CDF cdf = CDF.of(distribution);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (int count = 0; count < 10; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar q = inverseCDF.quantile(cdf.p_lessEquals(x));
      Tolerance.CHOP.requireClose(x, q);
    }
  }

  public void testComplexFail() {
    AssertFail.of(() -> NormalDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km")));
    AssertFail.of(() -> NormalDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m")));
    AssertFail.of(() -> NormalDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m")));
  }

  public void testNegativeSigmaFail() {
    NormalDistribution.of(5, 1);
    AssertFail.of(() -> NormalDistribution.of(5, -1));
  }
}
