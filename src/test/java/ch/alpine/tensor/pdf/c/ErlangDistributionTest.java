// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

class ErlangDistributionTest {
  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ErlangDistribution.of(3, 1.8));
    PDF pdf = PDF.of(distribution);
    Scalar p = pdf.at(RealScalar.of(3.2));
    Chop._06.requireClose(p, RealScalar.of(0.0940917));
    assertEquals(pdf.at(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(-0.12)), RealScalar.ZERO);
    for (int k = 3; k < 100; k += 4) {
      Scalar prob = pdf.at(Power.of(10, k));
      assertEquals(prob, RealScalar.ZERO);
    }
  }

  @Test
  void testKOne() {
    Distribution distribution = ErlangDistribution.of(1, RealScalar.of(3.2));
    assertInstanceOf(ExponentialDistribution.class, distribution);
  }

  @Test
  void testExpCase() {
    Scalar lambda = RealScalar.of(0.3);
    Distribution d1 = ExponentialDistribution.of(lambda);
    Distribution d2 = new ErlangDistribution(1, lambda);
    PDF pdf1 = PDF.of(d1);
    PDF pdf2 = PDF.of(d2);
    Tensor samples = Subdivide.of(0.1, 10.0, 14);
    Tolerance.CHOP.requireClose(samples.maps(pdf1::at), samples.maps(pdf2::at));
  }

  @Test
  void testMean() {
    Distribution distribution = ErlangDistribution.of(5, Quantity.of(10, "m"));
    Scalar mean = Expectation.mean(distribution);
    assertEquals(mean, Scalars.fromString("1/2[m^-1]"));
  }

  @Test
  void testVariance() {
    Distribution distribution = ErlangDistribution.of(5, Quantity.of(10, "m"));
    Scalar var = Expectation.variance(distribution);
    assertEquals(var, Scalars.fromString("1/20[m^-2]"));
    Tolerance.CHOP.requireClose(StandardDeviation.of(distribution), Sqrt.FUNCTION.apply(var));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    for (Tensor _p : Subdivide.of(0, 1, 10)) {
      Scalar p = (Scalar) _p;
      Scalar scalar = inverseCDF.quantile(p);
      CDF cdf = CDF.of(distribution);
      Scalar q = cdf.p_lessThan(scalar);
      Tolerance.CHOP.requireClose(p, q);
    }
    assertThrows(Exception.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(Exception.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
  }

  @Test
  void testQuantityPDF() {
    Distribution distribution = ErlangDistribution.of(4, Quantity.of(6, "m"));
    PDF pdf = PDF.of(distribution);
    {
      Scalar prob = pdf.at(Quantity.of(1.2, "m^-1"));
      assertEquals(QuantityUnit.of(prob), Unit.of("m"));
    }
    {
      Scalar prob = pdf.at(Quantity.of(-1.2, "m^-1"));
      assertInstanceOf(Quantity.class, prob);
      assertEquals(QuantityUnit.of(prob), Unit.of("m"));
    }
  }

  @Test
  void testInverseCDF() {
    Distribution distribution = ErlangDistribution.of(3, 1.8);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Scalar p = RationalScalar.of(3, 10);
    Scalar scalar = inverseCDF.quantile(p);
    CDF cdf = CDF.of(distribution);
    Scalar q = cdf.p_lessThan(scalar);
    Tolerance.CHOP.requireClose(p, q);
    assertThrows(Exception.class, () -> inverseCDF.quantile(RealScalar.of(-0.1)));
    assertThrows(Exception.class, () -> inverseCDF.quantile(RealScalar.of(+1.1)));
    RandomVariate.of(distribution);
    UnivariateDistribution ud = (UnivariateDistribution) distribution;
    assertEquals(ud.support(), Clips.positive(Double.POSITIVE_INFINITY));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(ErlangDistribution.of(1, 0.3));
  }

  @Test
  void testToString() {
    Distribution distribution = ErlangDistribution.of(5, Quantity.of(10, "m"));
    assertEquals(distribution.toString(), "ErlangDistribution[5, 10[m]]");
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> ErlangDistribution.of(0, RealScalar.of(1.8)));
  }
}
