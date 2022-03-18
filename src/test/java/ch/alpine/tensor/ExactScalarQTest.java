// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class ExactScalarQTest {
  @Test
  public void testPositive() {
    assertTrue(ExactScalarQ.of(RealScalar.ZERO));
    assertTrue(ExactScalarQ.of(RationalScalar.of(2, 3)));
    assertTrue(ExactScalarQ.of(GaussScalar.of(4, 7)));
  }

  @Test
  public void testNegative() {
    assertFalse(ExactScalarQ.of(DoubleScalar.of(0)));
    assertFalse(ExactScalarQ.of(DecimalScalar.of(new BigDecimal("0"))));
    assertFalse(ExactScalarQ.of(DoubleScalar.of(3.14)));
    assertFalse(ExactScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(ExactScalarQ.of(DoubleScalar.INDETERMINATE));
  }

  @Test
  public void testDecimal() {
    assertFalse(ExactScalarQ.of(DecimalScalar.of(new BigDecimal("3.14"))));
    assertFalse(ExactScalarQ.of(DecimalScalar.of(new BigDecimal("31234"))));
  }

  @Test
  public void testComplex() {
    assertTrue(ExactScalarQ.of(ComplexScalar.of(3, 4)));
    assertFalse(ExactScalarQ.of(ComplexScalar.of(3., 4)));
    assertFalse(ExactScalarQ.of(ComplexScalar.of(3, 4.)));
  }

  @Test
  public void testQuantity() {
    assertTrue(ExactScalarQ.of(Quantity.of(3, "m")));
    assertFalse(ExactScalarQ.of(Quantity.of(2.71, "kg*s")));
  }

  @Test
  public void testAny() {
    assertTrue(ExactScalarQ.any(Tensors.vector(1., 1, 1.)));
    assertFalse(ExactScalarQ.any(Tensors.vectorDouble(1, 2, 3)));
  }

  @Test
  public void testRequire() {
    Scalar scalar = ExactScalarQ.require(RationalScalar.of(3, 7));
    assertEquals(scalar, RationalScalar.of(3, 7));
  }

  @Test
  public void testRequireFail() {
    AssertFail.of(() -> ExactScalarQ.require(DoubleScalar.of(3)));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> ExactScalarQ.of(null));
  }
}
