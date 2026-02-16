// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.sca.Chop;

class BetaTest {
  @Test
  void testExact() {
    Scalar beta = Beta.of(5, 4);
    Scalar exact = Rational.of(1, 280);
    Chop._14.requireClose(beta, exact);
  }

  @Test
  void testNumeric() {
    Scalar beta = Beta.of(2.3, 3.2);
    Scalar exact = RealScalar.of(0.05402979174835722);
    Chop._14.requireClose(beta, exact);
  }

  @Test
  void testVectorFail() {
    assertThrows(ClassCastException.class, () -> Beta.of(HilbertMatrix.of(3)));
  }
}
