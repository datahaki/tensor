// code by jph
package ch.alpine.tensor.sca.exp;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class LogcTest {
  @Test
  void testSimple() {
    Scalar scalar = Logc.FUNCTION.apply(RealScalar.of(1 + 1e-13));
    Chop._08.requireClose(scalar, RealScalar.ONE);
  }

  @Test
  void testFraction() {
    Scalar dl1 = RandomVariate.of(NormalDistribution.standard());
    Scalar dl2 = RandomVariate.of(NormalDistribution.standard());
    Scalar l1 = Exp.FUNCTION.apply(dl1);
    Scalar l2 = Exp.FUNCTION.apply(dl2);
    Scalar lhs = Logc.FUNCTION.apply(l1.divide(l2));
    Scalar rhs = dl1.subtract(dl2).divide(l1.divide(l2).subtract(RealScalar.ONE));
    Tolerance.CHOP.requireClose(lhs, rhs);
  }

  static Scalar Logc_evaluate(Scalar lambda) {
    Scalar den = lambda.subtract(RealScalar.ONE);
    return Log.FUNCTION.apply(lambda).divide(den);
  }

  @Test
  void testRandom() {
    Distribution distribution = UniformDistribution.of(0, 2e-10);
    for (int count = 0; count < 100; ++count) {
      Scalar mu = RealScalar.ONE.add(RandomVariate.of(distribution));
      Chop._06.requireClose( //
          Logc.FUNCTION.apply(mu), //
          RealScalar.ONE);
    }
  }
}
