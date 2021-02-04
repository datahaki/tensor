// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class ExactBlackmanWindowTest extends TestCase {
  public void testSimple() {
    Tolerance.CHOP.requireClose( //
        ExactBlackmanWindow.FUNCTION.apply(RealScalar.of(0.2)), //
        RealScalar.of(0.5178645059151086));
    Tolerance.CHOP.requireClose( //
        ExactBlackmanWindow.FUNCTION.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.006878761822871883));
  }
}
