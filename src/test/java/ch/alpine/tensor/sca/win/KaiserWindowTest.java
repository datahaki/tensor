// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;

class KaiserWindowTest {
  @Test
  void test() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator suo = Serialization.copy(KaiserWindow.of(2));
    Scalar scalar = suo.apply(RealScalar.of(0.3));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.7676749968275237));
    assertTrue(suo.toString().startsWith("KaiserWindow["));
  }
}
