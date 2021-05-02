// code by jph
package ch.alpine.tensor.sca.win;

import java.util.Map;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TukeyWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = TukeyWindow.of(RealScalar.of(0.45));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.4)), //
        RealScalar.of(0.6710100716628344));
    Tolerance.CHOP.requireClose( //
        suo.apply(RealScalar.of(0.5)), //
        RealScalar.of(0.32898992833716567));
  }

  public void testSmall() {
    Tensor tensor = Tensors.of(RationalScalar.of(-1, 6), RealScalar.ZERO, RealScalar.of(0.01), RationalScalar.of(1, 6));
    Tensor mapped = tensor.map(TukeyWindow.FUNCTION);
    Map<Tensor, Long> map = Tally.of(mapped);
    assertEquals(map.get(RealScalar.ONE).longValue(), tensor.length());
  }

  public void testNumerical() {
    ScalarUnaryOperator scalarUnaryOperator = TukeyWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(0.12)), RealScalar.ONE);
    Scalar scalar = scalarUnaryOperator.apply(RealScalar.of(0.22));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(0.9381533400219317)); // mathematica
  }

  public void testSemiExact() {
    Scalar scalar = TukeyWindow.FUNCTION.apply(RealScalar.of(0.5));
    assertTrue(Scalars.isZero(scalar));
  }

  public void testOutside() {
    Scalar scalar = TukeyWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  public void testQuantityFail() {
    AssertFail.of(() -> TukeyWindow.FUNCTION.apply(Quantity.of(0, "s")));
    AssertFail.of(() -> TukeyWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }

  public void testNullFail() {
    AssertFail.of(() -> TukeyWindow.of(null));
  }
}
