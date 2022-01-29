// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sqrt;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TriangularDistributionTest extends TestCase {
  public void testPdf() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Scalar c = RealScalar.of(3);
    Distribution distribution = TriangularDistribution.of(a, b, c);
    assertEquals(PDF.of(distribution).at(RationalScalar.of(3, 2)), RationalScalar.HALF);
    assertEquals(PDF.of(distribution).at(RationalScalar.of(5, 2)), RationalScalar.HALF);
    assertEquals(PDF.of(distribution).at(b), RealScalar.ONE);
    assertEquals(PDF.of(distribution).at(RealScalar.of(100)), RealScalar.ZERO);
  }

  public void testCdf() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Scalar c = RealScalar.of(3);
    Distribution distribution = TriangularDistribution.of(a, b, c);
    assertEquals(CDF.of(distribution).p_lessThan(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessThan(a), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessEquals(a), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessThan(b), RationalScalar.HALF);
    assertEquals(CDF.of(distribution).p_lessEquals(b), RationalScalar.HALF);
    assertEquals(CDF.of(distribution).p_lessThan(RationalScalar.of(5, 2)), RationalScalar.of(7, 8));
    assertEquals(CDF.of(distribution).p_lessThan(c), RealScalar.ONE);
    assertEquals(CDF.of(distribution).p_lessEquals(c), RealScalar.ONE);
  }

  public void testMean() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Scalar c = RealScalar.of(3);
    Distribution distribution = TriangularDistribution.of(a, b, c);
    Scalar mean = (Scalar) Mean.of(RandomVariate.of(distribution, 100));
    Clips.interval(1.5, 2.5).requireInside(mean);
    assertEquals(Mean.of(distribution), RealScalar.of(2));
  }

  public void testExactFail() {
    TriangularDistribution.of(RealScalar.of(3), RealScalar.of(3), RealScalar.of(5));
    TriangularDistribution.of(RealScalar.of(3), RealScalar.of(5), RealScalar.of(5));
    AssertFail.of(() -> TriangularDistribution.of(RealScalar.of(3), RealScalar.of(3), RealScalar.of(3)));
    AssertFail.of(() -> TriangularDistribution.of(RealScalar.of(3), RealScalar.of(4), RealScalar.of(3)));
  }

  public void testNumericFail() {
    TriangularDistribution.of(RealScalar.of(3.), RealScalar.of(3.), RealScalar.of(5.));
    TriangularDistribution.of(RealScalar.of(3.), RealScalar.of(5.), RealScalar.of(5.));
    AssertFail.of(() -> TriangularDistribution.of(RealScalar.of(3.), RealScalar.of(3.), RealScalar.of(3.)));
    AssertFail.of(() -> TriangularDistribution.of(RealScalar.of(3.), RealScalar.of(4.), RealScalar.of(3.)));
  }

  public void testPdfLo() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Distribution distribution = TriangularDistribution.of(a, a, b);
    assertEquals(PDF.of(distribution).at(a), RealScalar.of(2));
    assertEquals(PDF.of(distribution).at(RationalScalar.of(3, 2)), RealScalar.ONE);
    assertEquals(PDF.of(distribution).at(RationalScalar.of(5, 2)), RealScalar.ZERO);
    assertEquals(PDF.of(distribution).at(RealScalar.of(100)), RealScalar.ZERO);
  }

  public void testCdfLo() {
    Scalar a = RealScalar.of(1);
    Scalar c = RealScalar.of(2);
    Distribution distribution = TriangularDistribution.of(a, a, c);
    assertEquals(CDF.of(distribution).p_lessThan(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessThan(a), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessEquals(a), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessThan(RationalScalar.of(3, 2)), RationalScalar.of(3, 4));
    assertEquals(CDF.of(distribution).p_lessThan(RationalScalar.of(5, 2)), RealScalar.ONE);
  }

  public void testMeanLo() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(1);
    Scalar c = RealScalar.of(2);
    Distribution distribution = TriangularDistribution.of(a, b, c);
    assertEquals(Mean.of(distribution), RationalScalar.of(4, 3));
    Scalar mean = (Scalar) Mean.of(RandomVariate.of(distribution, 100));
    Clips.interval(1.2, 1.5).requireInside(mean);
  }

  public void testPdfHi() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Distribution distribution = TriangularDistribution.of(a, b, b);
    assertEquals(PDF.of(distribution).at(RealScalar.ONE), RealScalar.ZERO);
    assertEquals(PDF.of(distribution).at(RationalScalar.of(3, 2)), RealScalar.ONE);
    assertEquals(PDF.of(distribution).at(RationalScalar.of(5, 2)), RealScalar.ZERO);
    assertEquals(PDF.of(distribution).at(b), RealScalar.of(2));
    assertEquals(PDF.of(distribution).at(RealScalar.of(100)), RealScalar.ZERO);
  }

  public void testCdfHi() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Distribution distribution = TriangularDistribution.of(a, b, b);
    assertEquals(CDF.of(distribution).p_lessThan(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessThan(a), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessEquals(a), RealScalar.ZERO);
    assertEquals(CDF.of(distribution).p_lessThan(RationalScalar.of(3, 2)), RationalScalar.of(1, 4));
    assertEquals(CDF.of(distribution).p_lessThan(b), RealScalar.ONE);
    assertEquals(CDF.of(distribution).p_lessEquals(b), RealScalar.ONE);
    assertEquals(CDF.of(distribution).p_lessThan(RationalScalar.of(5, 2)), RealScalar.ONE);
  }

  public void testMeanHi() {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Distribution distribution = TriangularDistribution.of(a, b, b);
    assertEquals(Mean.of(distribution), RationalScalar.of(5, 3));
    Scalar mean = (Scalar) Mean.of(RandomVariate.of(distribution, 100));
    Clips.interval(1.5, 1.8).requireInside(mean);
  }

  public void testWith() {
    Distribution distribution = TriangularDistribution.with(0, 1);
    Scalar value = PDF.of(distribution).at(RealScalar.ZERO);
    Tolerance.CHOP.requireClose(value, Sqrt.FUNCTION.apply(RealScalar.of(6).reciprocal()));
  }

  public void testWithMean() {
    Distribution distribution = TriangularDistribution.with(10, 1);
    Tolerance.CHOP.requireClose(Mean.of(distribution), RealScalar.of(10));
    Scalar value = PDF.of(distribution).at(RealScalar.of(10));
    Tolerance.CHOP.requireClose(value, Sqrt.FUNCTION.apply(RealScalar.of(6).reciprocal()));
    Tolerance.CHOP.requireClose(Variance.of(distribution), RealScalar.ONE);
  }

  public void testQuantity() {
    Distribution distribution = TriangularDistribution.with(Quantity.of(3, "m"), Quantity.of(2, "m"));
    PDF.of(distribution).at(Quantity.of(3.3, "m"));
    assertEquals(Mean.of(distribution), Quantity.of(3, "m"));
    Scalar variance = Variance.of(distribution);
    // Tolerance.CHOP.requireClose(variance, Quantity.of(4, "m"));
  }

  public void testWithFail() {
    AssertFail.of(() -> TriangularDistribution.with(RealScalar.of(0), RealScalar.of(0)));
    AssertFail.of(() -> TriangularDistribution.with(RealScalar.of(0), RealScalar.of(-1)));
  }
}
