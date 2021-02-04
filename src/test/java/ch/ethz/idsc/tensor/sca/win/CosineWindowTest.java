// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CosineWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator connesWindow = CosineWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.15274668107476286));
    Tolerance.CHOP.requireZero( //
        connesWindow.apply(RealScalar.of(0.5)));
  }

  public void testNullFail() {
    AssertFail.of(() -> CosineWindow.of(null));
  }
}
