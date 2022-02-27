// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import junit.framework.TestCase;

public class StrassenTest extends TestCase {
  public void testSimple() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    for (int count = 12; count < 25; count += 2) {
      Tensor a = RandomVariate.of(distribution, count, count);
      Tensor b = RandomVariate.of(distribution, count, count);
      Tensor dot1 = new Strassen(16).apply(a, b);
      Tensor dot2 = a.dot(b);
      assertEquals(dot1, dot2);
    }
  }

  public void testHuge() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    int count = 32;
    Tensor a = RandomVariate.of(distribution, count, count);
    Tensor b = RandomVariate.of(distribution, count, count);
    Tensor dot1;
    Tensor dot2;
    {
      Timing started2 = Timing.started();
      dot1 = a.dot(b);
      started2.seconds();
    }
    {
      Timing started1 = Timing.started();
      dot2 = new Strassen(16).apply(a, b);
      started1.seconds();
    }
    Tolerance.CHOP.requireClose(dot1, dot2);
  }
}
