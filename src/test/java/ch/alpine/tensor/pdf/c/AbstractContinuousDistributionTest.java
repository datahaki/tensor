// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;

class AbstractContinuousDistributionTest {
  @Test
  void testEquals() {
    Distribution d1 = NormalDistribution.of(1, 2);
    Distribution d2 = NormalDistribution.of(1, 3);
    Distribution d3 = BinomialDistribution.of(3, RealScalar.of(0.2));
    assertNotEquals(d1, d2);
    assertNotEquals(d1, d3);
    assertNotEquals(d3, d1);
  }

  @Test
  void testHistogram() {
    Distribution d1 = HistogramDistribution.of(Tensors.vector(1.2, 0.3, 0.11, 2.2, 1.4, 5.0, 1.23, 0.1));
    Distribution d2 = NormalDistribution.of(1, 3);
    assertNotEquals(d1, d2);
    assertNotEquals(d2, d1);
  }

  @Test
  void testHistogram2() {
    Distribution d1 = HistogramDistribution.of(Tensors.vector(1.2, 0.3, 0.11, 2.2, 1.4, 5.0, 1.23, 0.1));
    Distribution d2 = HistogramDistribution.of(Tensors.vector(1.2, 0.3, 0.11, 2.2, 1.4, 5.0, 1.23, 0.1), RealScalar.of(2));
    Distribution d3 = NormalDistribution.of(1, 3);
    assertNotEquals(d1, d2);
    assertNotEquals(d1, d3);
    assertNotEquals(d3, d1);
  }
}
