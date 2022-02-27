// code by jph
package ch.alpine.tensor.sca;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SoftThresholdTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator deadzone = Serialization.copy(SoftThreshold.of(Clips.interval(-2, 1)));
    assertEquals(deadzone.apply(RealScalar.of(3)), RealScalar.of(2));
    assertEquals(deadzone.apply(RealScalar.of(-3)), RealScalar.of(-1));
    assertEquals(deadzone.apply(RealScalar.of(-2)), RealScalar.of(0));
    assertEquals(deadzone.apply(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(deadzone.apply(RealScalar.of(0.5)), RealScalar.of(0));
    assertEquals(deadzone.apply(RealScalar.of(1.5)), RealScalar.of(0.5));
  }

  public void testNullFail() {
    AssertFail.of(() -> SoftThreshold.of(null));
  }
}
