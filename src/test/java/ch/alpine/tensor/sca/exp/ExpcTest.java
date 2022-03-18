// code by jph
package ch.alpine.tensor.sca.exp;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

public class ExpcTest {
  @Test
  public void testSimple() {
    Scalar scalar = Expc.FUNCTION.apply(RealScalar.of(-1e-13));
    Chop._02.requireClose(scalar, RealScalar.ONE);
  }

  @Test
  public void testEps() {
    Scalar scalar = Expc.FUNCTION.apply(RealScalar.of(Double.MIN_VALUE));
    Tolerance.CHOP.requireClose(scalar, RealScalar.ONE);
  }

  @Test
  public void testRandom() {
    Distribution distribution = UniformDistribution.of(0, 2e-12);
    for (int count = 0; count < 100; ++count) {
      Scalar mu = RandomVariate.of(distribution);
      Chop._10.requireClose( //
          Expc.FUNCTION.apply(mu), //
          RealScalar.ONE);
    }
  }
}
