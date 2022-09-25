// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;

class ErlangDistributionTest {
  @Test
  void testPdf() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(ErlangDistribution.of(3, 1.8));
    PDF pdf = PDF.of(distribution);
    Scalar p = pdf.at(RealScalar.of(3.2));
    Chop._06.requireClose(p, RealScalar.of(0.0940917));
    assertEquals(pdf.at(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pdf.at(RealScalar.of(-0.12)), RealScalar.ZERO);
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
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(ErlangDistribution.of(100, 1.8));
    // TestMarkovChebyshev.monotonous(ErlangDistribution.of(100, 1E-100)); // does not work
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
