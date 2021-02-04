// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class HannPoissonWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = HannPoissonWindow.of(RealScalar.of(1.3));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.03375191875636745));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0));
  }
}
