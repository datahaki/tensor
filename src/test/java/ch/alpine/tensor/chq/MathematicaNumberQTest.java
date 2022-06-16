// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class MathematicaNumberQTest {
  @Test
  void testRealFinite() {
    assertTrue(MathematicaNumberQ.of(RealScalar.of(0.)));
    assertTrue(MathematicaNumberQ.of(RealScalar.ZERO));
  }

  @Test
  void testComplex() {
    assertTrue(MathematicaNumberQ.of(ComplexScalar.of(0., 0.3)));
    assertTrue(MathematicaNumberQ.of(ComplexScalar.of(0., 2)));
  }

  @Test
  void testComplexCorner() {
    assertFalse(MathematicaNumberQ.of(ComplexScalar.of(Double.POSITIVE_INFINITY, 0.3)));
    assertFalse(MathematicaNumberQ.of(ComplexScalar.of(0., Double.NaN)));
  }

  @Test
  void testGauss() {
    assertTrue(MathematicaNumberQ.of(GaussScalar.of(3, 7)));
    assertTrue(MathematicaNumberQ.of(GaussScalar.of(0, 7)));
  }

  @Test
  void testCorner() {
    assertFalse(MathematicaNumberQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(MathematicaNumberQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(MathematicaNumberQ.of(RealScalar.of(Double.NaN)));
    assertFalse(MathematicaNumberQ.of(DoubleScalar.INDETERMINATE));
  }

  @Test
  void testCornerFloat() {
    assertFalse(MathematicaNumberQ.of(RealScalar.of(Float.POSITIVE_INFINITY)));
    assertFalse(MathematicaNumberQ.of(RealScalar.of(Float.POSITIVE_INFINITY)));
    assertFalse(MathematicaNumberQ.of(RealScalar.of(Float.NaN)));
  }

  @Test
  void testQuantity() {
    assertFalse(MathematicaNumberQ.of(Quantity.of(3, "m")));
    assertFalse(MathematicaNumberQ.of(Quantity.of(3.14, "m")));
  }

  @Test
  void testAll() {
    assertTrue(MathematicaNumberQ.all(Tensors.fromString("{1, 3}")));
    assertFalse(MathematicaNumberQ.all(Tensors.fromString("{1, 3[m]}")));
  }

  @Test
  void testRequire() {
    Scalar scalar = MathematicaNumberQ.require(RealScalar.of(123.456));
    assertEquals(scalar, RealScalar.of(123.456));
  }

  @Test
  void testRequireFail() {
    assertThrows(TensorRuntimeException.class, () -> MathematicaNumberQ.require(Quantity.of(6, "apples")));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> MathematicaNumberQ.of(null));
  }
}
