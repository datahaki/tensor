// code by jph
package ch.alpine.tensor.sca.exp;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.mat.Tolerance;

class GeluTest {
  @Test
  void testFandDxSync() {
    Scalar x = RealScalar.of(0.3);
    JetScalar jetScalar = JetScalar.of(x, 2);
    JetScalar result = (JetScalar) Gelu.FUNCTION.apply(jetScalar);
    Scalar dx = Gelu.dx(x);
    Scalar expect = result.vector().Get(1);
    Tolerance.CHOP.requireClose(dx, expect);
  }
}
