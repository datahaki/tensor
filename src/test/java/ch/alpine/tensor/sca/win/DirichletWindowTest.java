// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.qty.Quantity;

class DirichletWindowTest {
  @Test
  public void testSimple() {
    ScalarUnaryOperator scalarUnaryOperator = DirichletWindow.FUNCTION;
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(+0.1)), RealScalar.ONE);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(+0.6)), RealScalar.ZERO);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(-0.1)), RealScalar.ONE);
    assertEquals(scalarUnaryOperator.apply(RealScalar.of(-0.6)), RealScalar.ZERO);
  }

  @Test
  public void testSemiExact() {
    Scalar scalar = DirichletWindow.FUNCTION.apply(RealScalar.of(0.5));
    assertTrue(Scalars.nonZero(scalar));
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testWindow() {
    ScalarUnaryOperator suo = DirichletWindow.FUNCTION;
    assertTrue(suo.equals(DirichletWindow.FUNCTION));
    assertEquals(suo, DirichletWindow.FUNCTION);
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> DirichletWindow.FUNCTION.apply(Quantity.of(0, "s")));
    assertThrows(TensorRuntimeException.class, () -> DirichletWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
