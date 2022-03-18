// code by jph
package ch.alpine.tensor.sca.win;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;

public class ExactBlackmanWindowTest {
  @Test
  public void testSimple() {
    Tolerance.CHOP.requireClose( //
        ExactBlackmanWindow.FUNCTION.apply(RealScalar.of(0.2)), //
        RealScalar.of(0.5178645059151086));
    Tolerance.CHOP.requireClose( //
        ExactBlackmanWindow.FUNCTION.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.006878761822871883));
  }
}
