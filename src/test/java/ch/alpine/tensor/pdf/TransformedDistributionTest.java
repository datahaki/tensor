// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.red.Mean;

class TransformedDistributionTest {
  @Test
  void testShifted() {
    Distribution d1 = DiscreteUniformDistribution.of(3, 11);
    Distribution d2 = TransformedDistribution.shift(DiscreteUniformDistribution.of(0, 8), RealScalar.of(3));
    Tensor domain = Range.of(0, 20);
    assertEquals(domain.map(PDF.of(d1)::at), domain.map(PDF.of(d2)::at));
    assertEquals(domain.map(CDF.of(d1)::p_lessThan), domain.map(CDF.of(d2)::p_lessThan));
    assertEquals(domain.map(CDF.of(d1)::p_lessEquals), domain.map(CDF.of(d2)::p_lessEquals));
    assertEquals(RandomVariate.of(d1, new Random(3), 10), RandomVariate.of(d2, new Random(3), 10));
    assertEquals(Mean.of(d1), Mean.of(d2));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> TransformedDistribution.shift(null, Pi.VALUE));
    assertThrows(Exception.class, () -> TransformedDistribution.shift(NormalDistribution.standard(), null));
  }
}
