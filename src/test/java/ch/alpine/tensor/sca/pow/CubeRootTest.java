// code by jph
package ch.alpine.tensor.sca.pow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;

class CubeRootTest {
  @Test
  void testSimple() {
    Scalar scalar = CubeRoot.FUNCTION.apply(RealScalar.of(27));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(3));
  }

  @Test
  void testQuantity() {
    Scalar input = Quantity.of(2, "m^3");
    Scalar scalar = CubeRoot.FUNCTION.apply(input);
    Tolerance.CHOP.requireClose(scalar, Quantity.of(1.2599210498948732, "m"));
    Tolerance.CHOP.requireClose(Times.of(scalar, scalar, scalar), input);
  }

  @Test
  void testNegative() {
    Scalar input = Quantity.of(-2, "m^3");
    Scalar scalar = CubeRoot.FUNCTION.apply(input);
    Tolerance.CHOP.requireClose(scalar, Quantity.of(-1.2599210498948731648, "m"));
  }

  @Test
  void testZero() {
    Scalar scalar = CubeRoot.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  @Test
  void testOf() {
    Tensor tensor = Tensors.vector(-27, -8, -1, 0, 1, 8, 27).map(CubeRoot.FUNCTION);
    assertEquals(tensor, Range.of(-3, 4));
  }

  @Test
  void testComplexFail() {
    Scalar scalar = ComplexScalar.of(12, 23);
    assertThrows(ClassCastException.class, () -> CubeRoot.FUNCTION.apply(scalar));
  }
}
