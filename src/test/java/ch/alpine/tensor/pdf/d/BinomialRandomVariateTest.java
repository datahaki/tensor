// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.DeleteDuplicates;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BinomialRandomVariateTest extends TestCase {
  public void testDivert() {
    assertEquals(BinomialDistribution.of(1200, DoubleScalar.of(0.5)).getClass(), BinomialRandomVariate.class);
    assertEquals(BinomialDistribution.of(1200, RationalScalar.of(1, 2)).getClass(), BinomialDistribution.class);
    assertEquals(BinomialDistribution.of(12000, DoubleScalar.of(0.1)).getClass(), BinomialRandomVariate.class);
    assertEquals(BinomialDistribution.of(120000, DoubleScalar.of(0.0001)).getClass(), BinomialRandomVariate.class);
  }

  public void testRandom() throws ClassNotFoundException, IOException {
    int n = 200;
    Distribution distribution = Serialization.copy(new BinomialRandomVariate(n, RealScalar.of(0.4)));
    Scalar value = RandomVariate.of(distribution);
    assertTrue(Sign.isPositive(value));
    assertTrue(Scalars.lessThan(value, RealScalar.of(n)));
    Scalar mean = Expectation.mean(distribution);
    assertEquals(mean, RealScalar.of(n * 0.4));
    Scalar var = Expectation.variance(distribution);
    assertEquals(var, RealScalar.of(n * 0.4 * 0.6));
  }

  public void testRandomVector() {
    int n = 200;
    Distribution distribution = new BinomialRandomVariate(n, RealScalar.of(0.4));
    Tensor tensor = RandomVariate.of(distribution, 100);
    Tensor unique = DeleteDuplicates.of(tensor);
    assertTrue(5 < unique.length());
  }

  public void testCorner() {
    Distribution distribution1 = BinomialDistribution.of(10, RealScalar.ONE);
    Distribution distribution2 = new BinomialRandomVariate(10, RealScalar.ONE);
    Scalar s1 = RandomVariate.of(distribution1);
    Scalar s2 = RandomVariate.of(distribution2);
    assertEquals(s1, s2);
    assertEquals(s2, RealScalar.of(10));
  }

  public void testPDFFail() {
    AssertFail.of(() -> PDF.of(BinomialDistribution.of(1200, DoubleScalar.of(0.5))));
  }

  public void testCDFFail() {
    AssertFail.of(() -> CDF.of(BinomialDistribution.of(1200, DoubleScalar.of(0.5))));
  }
}
