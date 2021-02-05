// code by jph
package ch.ethz.idsc.tensor.sca.win;

import java.io.IOException;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class GaussianWindowTest extends TestCase {
  public void testSimple() {
    Scalar apply = GaussianWindow.FUNCTION.apply(RealScalar.of(0.2));
    Scalar exact = RealScalar.of(0.80073740291680804078);
    Chop._10.requireClose(apply, exact);
  }

  public void testOutside() throws ClassNotFoundException, IOException {
    Scalar scalar = Serialization.copy(GaussianWindow.FUNCTION).apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
    assertEquals(GaussianWindow.FUNCTION.toString(), "GaussianWindow[3/10]");
  }

  public void testCustom() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator copy = Serialization.copy(GaussianWindow.of(RationalScalar.of(2, 10)));
    Scalar apply = copy.apply(RealScalar.of(0.4));
    Scalar exact = RealScalar.of(0.13533528323661262);
    Chop._10.requireClose(apply, exact);
  }

  public void testToString() {
    assertEquals(GaussianWindow.FUNCTION.toString(), "GaussianWindow[3/10]");
  }

  public void testNullFail() {
    AssertFail.of(() -> GaussianWindow.of(null));
  }
}
