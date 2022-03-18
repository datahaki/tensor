// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class NumberQTest {
  @Test
  public void testRealFinite() {
    assertTrue(NumberQ.of(RealScalar.of(0.)));
    assertTrue(NumberQ.of(RealScalar.ZERO));
  }

  @Test
  public void testComplex() {
    assertTrue(NumberQ.of(ComplexScalar.of(0., 0.3)));
    assertTrue(NumberQ.of(ComplexScalar.of(0., 2)));
  }

  @Test
  public void testComplexCorner() {
    assertFalse(NumberQ.of(ComplexScalar.of(Double.POSITIVE_INFINITY, 0.3)));
    assertFalse(NumberQ.of(ComplexScalar.of(0., Double.NaN)));
  }

  @Test
  public void testGauss() {
    assertTrue(NumberQ.of(GaussScalar.of(3, 7)));
    assertTrue(NumberQ.of(GaussScalar.of(0, 7)));
  }

  @Test
  public void testCorner() {
    assertFalse(NumberQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(NumberQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(NumberQ.of(RealScalar.of(Double.NaN)));
    assertFalse(NumberQ.of(DoubleScalar.INDETERMINATE));
  }

  @Test
  public void testCornerFloat() {
    assertFalse(NumberQ.of(RealScalar.of(Float.POSITIVE_INFINITY)));
    assertFalse(NumberQ.of(RealScalar.of(Float.POSITIVE_INFINITY)));
    assertFalse(NumberQ.of(RealScalar.of(Float.NaN)));
  }

  @Test
  public void testQuantity() {
    assertFalse(NumberQ.of(Quantity.of(3, "m")));
    assertFalse(NumberQ.of(Quantity.of(3.14, "m")));
  }

  @Test
  public void testAll() {
    assertTrue(NumberQ.all(Tensors.fromString("{1, 3}")));
    assertFalse(NumberQ.all(Tensors.fromString("{1, 3[m]}")));
  }

  @Test
  public void testRequire() {
    Scalar scalar = NumberQ.require(RealScalar.of(123.456));
    assertEquals(scalar, RealScalar.of(123.456));
  }

  @Test
  public void testRequireFail() {
    AssertFail.of(() -> NumberQ.require(Quantity.of(6, "apples")));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> NumberQ.of(null));
  }
}
