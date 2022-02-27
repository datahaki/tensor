// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import junit.framework.TestCase;

public class MixtureDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution d = BernoulliDistribution.of(RationalScalar.HALF);
    Distribution d1 = Serialization.copy(MixtureDistribution.of(Tensors.vector(1, 2, 3), d, d, d));
    Distribution d2 = BernoulliDistribution.of(RationalScalar.HALF);
    Tensor domain = Range.of(-1, 3);
    assertEquals(domain.map(PDF.of(d1)::at), domain.map(PDF.of(d2)::at));
  }
}
