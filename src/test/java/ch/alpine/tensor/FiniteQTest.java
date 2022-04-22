// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

public class FiniteQTest {
  @Test
  public void testRealFinite() {
    assertTrue(FiniteQ.of(RealScalar.of(0.)));
    assertTrue(FiniteQ.of(RealScalar.ZERO));
  }

  @Test
  public void testComplex() {
    assertTrue(FiniteQ.of(ComplexScalar.of(0., 0.3)));
    assertTrue(FiniteQ.of(ComplexScalar.of(0., 2)));
  }

  @Test
  public void testComplexCorner() {
    assertFalse(FiniteQ.of(ComplexScalar.of(Double.POSITIVE_INFINITY, 0.3)));
    assertFalse(FiniteQ.of(ComplexScalar.of(0., Double.NaN)));
  }

  @Test
  public void testGauss() {
    assertTrue(FiniteQ.of(GaussScalar.of(3, 7)));
    assertTrue(FiniteQ.of(GaussScalar.of(0, 7)));
  }

  @Test
  public void testCorner() {
    assertFalse(FiniteQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(FiniteQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(FiniteQ.of(RealScalar.of(Double.NaN)));
  }

  @Test
  public void testQuantity() {
    assertTrue(FiniteQ.of(Quantity.of(3, "m")));
    assertTrue(FiniteQ.of(Quantity.of(3.1415, "m")));
  }

  @Test
  public void testAll() {
    assertTrue(FiniteQ.all(Tensors.vector(1, 1, 1.)));
    assertTrue(FiniteQ.all(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> FiniteQ.of(null));
  }
}
