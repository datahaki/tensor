// code by jph
package ch.alpine.tensor.sca.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class LogisticSigmoidTest {
  @Test
  void testBasic() {
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.ZERO), RealScalar.of(0.5));
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(0.)), RealScalar.of(0.5));
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(1e3)), RealScalar.ONE);
    assertEquals(LogisticSigmoid.FUNCTION.apply(RealScalar.of(-1e3)), RealScalar.ZERO);
  }

  @Test
  void testMathematica() {
    Scalar big = LogisticSigmoid.FUNCTION.apply(RealScalar.of(0.5));
    assertTrue(big.toString().startsWith("0.622459")); // from Mathematica
  }
}
