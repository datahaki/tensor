// code by jph
package ch.alpine.tensor.sca.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class LogisticSigmoidTest {
  @Test
  void testBasic() {
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.ZERO), RealScalar.of(0.5));
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(0.)), RealScalar.of(0.5));
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(1e3)), RealScalar.ONE);
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(-1e3)), RealScalar.ZERO);
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(1e10)), RealScalar.ONE);
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(-1e10)), RealScalar.ZERO);
  }

  @Test
  void testMathematica() {
    Scalar big = LogisticSigmoid.FUNCTION.apply(RealScalar.of(0.5));
    assertTrue(big.toString().startsWith("0.622459")); // from Mathematica
  }

  @Test
  void test() {
    Scalar t = RandomVariate.of(NormalDistribution.standard());
    JetScalar jetScalar = JetScalar.identity(t, 2);
    Scalar f0 = LogisticSigmoid.FUNCTION.apply(jetScalar);
    Scalar d0 = LogisticSigmoid.DERIVATIVE.apply(t);
    JetScalar js = (JetScalar) f0;
    Tolerance.CHOP.requireClose(js.jet().Get(1), d0);
  }

  @Test
  void testNested() {
    Scalar t = RandomVariate.of(NormalDistribution.standard());
    JetScalar jetScalar = JetScalar.identity(t, 2);
    Scalar f0 = LogisticSigmoid.FUNCTION.apply(jetScalar);
    ScalarUnaryOperator NESTED = y -> y.multiply(RealScalar.ONE.subtract(y)); // x * (1 - x)
    Scalar d0 = NESTED.apply(f0);
    Scalar d1 = LogisticSigmoid.DERIVATIVE.apply(t);
    JetScalar df = (JetScalar) d0;
    Tolerance.CHOP.requireClose(df.jet().Get(0), d1);
  }
}
