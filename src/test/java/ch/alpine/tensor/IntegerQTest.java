// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class IntegerQTest {
  @Test
  public void testPositive() {
    assertTrue(IntegerQ.of(Scalars.fromString("9/3")));
    assertTrue(IntegerQ.of(Scalars.fromString("-529384765923478653476593847659876237486")));
    assertTrue(IntegerQ.of(ComplexScalar.of(-2, 0)));
  }

  @Test
  public void testNegative() {
    assertFalse(IntegerQ.of(Scalars.fromString("9.0")));
    assertFalse(IntegerQ.of(ComplexScalar.of(2, 3)));
    assertFalse(IntegerQ.of(Scalars.fromString("abc")));
  }

  @Test
  public void testRequire() {
    Scalar scalar = RealScalar.of(2);
    assertTrue(IntegerQ.require(scalar) == scalar);
  }

  @Test
  public void testRequireFail() {
    AssertFail.of(() -> IntegerQ.require(RealScalar.of(0.2)));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> IntegerQ.of(null));
  }
}
