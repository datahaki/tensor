// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class StrassenTest {
  @Test
  void testSimple() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int count = 12; count < 25; count += 2) {
      Tensor a = RandomVariate.of(distribution, count, count);
      Tensor b = RandomVariate.of(distribution, count, count);
      Tensor dot1 = new Strassen(16).apply(a, b);
      Tensor dot2 = a.dot(b);
      assertEquals(dot1, dot2);
    }
  }

  @Test
  void testHuge() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    int count = 32;
    Tensor a = RandomVariate.of(distribution, count, count);
    Tensor b = RandomVariate.of(distribution, count, count);
    Tensor dot1;
    Tensor dot2;
    dot1 = a.dot(b);
    dot2 = new Strassen(16).apply(a, b);
    Tolerance.CHOP.requireClose(dot1, dot2);
  }
}
