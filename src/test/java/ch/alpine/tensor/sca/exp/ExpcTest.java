// code by jph
package ch.alpine.tensor.sca.exp;

import java.math.BigDecimal;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

class ExpcTest {
  @Test
  void testSimple() {
    Scalar scalar = Expc.FUNCTION.apply(RealScalar.of(-1e-13));
    Chop._02.requireClose(scalar, RealScalar.ONE);
  }

  @Test
  void testEps() {
    Scalar scalar = Expc.FUNCTION.apply(RealScalar.of(Double.MIN_VALUE));
    Tolerance.CHOP.requireClose(scalar, RealScalar.ONE);
  }

  @RepeatedTest(5)
  void testRandom() {
    Distribution distribution = UniformDistribution.of(0, 2e-12);
    Scalar mu = RandomVariate.of(distribution);
    Chop._10.requireClose( //
        Expc.FUNCTION.apply(mu), //
        RealScalar.ONE);
  }

  @RepeatedTest(10)
  void testDecimal(RepetitionInfo repetitionInfo) {
    int n = 12 + repetitionInfo.getCurrentRepetition();
    BigDecimal bigDecimal = new BigDecimal("0." + "0".repeat(n) + "123");
    Scalar scalar = RealScalar.of(bigDecimal);
    Scalar result = Expc.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(RealScalar.ONE, result);
  }

  @Test
  void test2ndCase() {
    Scalar scalar = Expc.FUNCTION.apply(RealScalar.of(2));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of((Math.exp(2) - 1) * 0.5));
  }
}
