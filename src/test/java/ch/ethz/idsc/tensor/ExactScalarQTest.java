// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ExactScalarQTest extends TestCase {
  public void testPositive() {
    assertTrue(ExactScalarQ.of(RealScalar.ZERO));
    assertTrue(ExactScalarQ.of(RationalScalar.of(2, 3)));
    assertTrue(ExactScalarQ.of(GaussScalar.of(4, 7)));
  }

  public void testNegative() {
    assertFalse(ExactScalarQ.of(DoubleScalar.of(0)));
    assertFalse(ExactScalarQ.of(DecimalScalar.of(0)));
    assertFalse(ExactScalarQ.of(DoubleScalar.of(3.14)));
    assertFalse(ExactScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(ExactScalarQ.of(DoubleScalar.INDETERMINATE));
  }

  public void testDecimal() {
    assertFalse(ExactScalarQ.of(DecimalScalar.of("3.14")));
    assertFalse(ExactScalarQ.of(DecimalScalar.of("31234")));
  }

  public void testComplex() {
    assertTrue(ExactScalarQ.of(ComplexScalar.of(3, 4)));
    assertFalse(ExactScalarQ.of(ComplexScalar.of(3., 4)));
    assertFalse(ExactScalarQ.of(ComplexScalar.of(3, 4.)));
  }

  public void testQuantity() {
    assertTrue(ExactScalarQ.of(Quantity.of(3, "m")));
    assertFalse(ExactScalarQ.of(Quantity.of(2.71, "kg*s")));
  }

  public void testAny() {
    assertTrue(ExactScalarQ.any(Tensors.vector(1., 1, 1.)));
    assertFalse(ExactScalarQ.any(Tensors.vectorDouble(1, 2, 3)));
  }

  public void testRequire() {
    Scalar scalar = ExactScalarQ.require(RationalScalar.of(3, 7));
    assertEquals(scalar, RationalScalar.of(3, 7));
  }

  public void testRequireFail() {
    AssertFail.of(() -> ExactScalarQ.require(DoubleScalar.of(3)));
  }

  public void testNullFail() {
    AssertFail.of(() -> ExactScalarQ.of(null));
  }
}
