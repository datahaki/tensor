// code by jph
package ch.alpine.tensor.lie.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactScalarQ;

class Det2DTest {
  @Test
  void testSimple() {
    assertEquals(Det2D.of(Tensors.vector(1, 0), Tensors.vector(0, 1)), RealScalar.ONE);
    assertEquals(Det2D.of(Tensors.vector(1, 1), Tensors.vector(0, 1)), RealScalar.ONE);
    assertEquals(Det2D.of(Tensors.vector(1, 2), Tensors.vector(0, 1)), RealScalar.ONE);
  }

  @Test
  void testMore() {
    assertEquals(Det2D.of(Tensors.vector(0, 1), Tensors.vector(1, 1)), RealScalar.ONE.negate());
  }

  @Test
  void testArea() {
    Scalar det = Det2D.of(UnitVector.of(2, 0), UnitVector.of(2, 1));
    assertEquals(det, RealScalar.ONE);
    assertTrue(ExactScalarQ.of(det));
  }

  @Test
  void testFailP() {
    assertThrows(IllegalArgumentException.class, () -> Det2D.of(Tensors.vector(1, 0), Tensors.vector(0, 1, 0)));
  }

  @Test
  void testFailQ() {
    assertThrows(Throw.class, () -> Det2D.of(Tensors.vector(1, 0, 0), Tensors.vector(0, 1)));
  }
}
