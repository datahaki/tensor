// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DirichletWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator scalarUnaryOperator = DirichletWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(+0.1)), RealScalar.ONE);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(+0.6)), RealScalar.ZERO);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(-0.1)), RealScalar.ONE);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(-0.6)), RealScalar.ZERO);
  }

  public void testSemiExact() {
    Scalar scalar = DirichletWindow.FUNCTION.apply(RealScalar.of(0.5));
    assertTrue(Scalars.nonZero(scalar));
    ExactScalarQ.require(scalar);
  }

  public void testWindow() {
    ScalarUnaryOperator suo = DirichletWindow.FUNCTION;
    assertTrue(suo.equals(DirichletWindow.FUNCTION));
    assertEquals(suo, DirichletWindow.FUNCTION);
  }

  public void testQuantityFail() {
    AssertFail.of(() -> DirichletWindow.FUNCTION.apply(Quantity.of(0, "s")));
    AssertFail.of(() -> DirichletWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
