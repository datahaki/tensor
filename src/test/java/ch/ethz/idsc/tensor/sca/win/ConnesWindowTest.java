// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ConnesWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator connesWindow = ConnesWindow.of(RealScalar.of(1.6));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.5625));
    Tolerance.CHOP.requireClose( //
        connesWindow.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.3713378906250001));
  }

  public void testExact() {
    Scalar scalar = ConnesWindow.of(RationalScalar.of(7, 8)).apply(RationalScalar.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  public void testNullFail() {
    AssertFail.of(() -> ConnesWindow.of(null));
  }
}
