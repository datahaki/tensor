// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IntegerQTest extends TestCase {
  public void testPositive() {
    assertTrue(IntegerQ.of(Scalars.fromString("9/3")));
    assertTrue(IntegerQ.of(Scalars.fromString("-529384765923478653476593847659876237486")));
    assertTrue(IntegerQ.of(ComplexScalar.of(-2, 0)));
  }

  public void testNegative() {
    assertFalse(IntegerQ.of(Scalars.fromString("9.0")));
    assertFalse(IntegerQ.of(ComplexScalar.of(2, 3)));
    assertFalse(IntegerQ.of(Scalars.fromString("abc")));
  }

  public void testRequire() {
    Scalar scalar = RealScalar.of(2);
    assertTrue(IntegerQ.require(scalar) == scalar);
  }

  public void testRequireFail() {
    AssertFail.of(() -> IntegerQ.require(RealScalar.of(0.2)));
  }

  public void testNullFail() {
    AssertFail.of(() -> IntegerQ.of(null));
  }
}
