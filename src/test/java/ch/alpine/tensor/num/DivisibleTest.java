// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class DivisibleTest {
  @Test
  public void testSimple() {
    assertTrue(Divisible.of(RealScalar.of(9), RealScalar.of(3)));
    assertTrue(Divisible.of(RationalScalar.of(18, 7), RationalScalar.of(3, 7)));
    assertFalse(Divisible.of(RationalScalar.of(8, 7), RationalScalar.of(3, 7)));
  }

  @Test
  public void testNegative() {
    assertTrue(Divisible.of(RealScalar.of(9), RealScalar.of(-3)));
    assertTrue(Divisible.of(RealScalar.of(-3), RealScalar.of(-3)));
  }

  @Test
  public void testComplex() {
    Scalar c2 = ComplexScalar.of(2, 3);
    Scalar c1 = c2.multiply(RealScalar.of(3));
    assertTrue(Divisible.of(c1, c2));
    assertFalse(Divisible.of(c2, c1));
  }

  @Test
  public void testGaussian() {
    Scalar c1 = ComplexScalar.of(3, 1);
    Scalar c2 = ComplexScalar.of(2, -1);
    assertTrue(Divisible.of(c1, c2));
    assertFalse(Divisible.of(c2, c1));
  }

  @Test
  public void testQuantity() {
    assertTrue(Divisible.of(Quantity.of(9, "m"), Quantity.of(3, "m")));
    assertFalse(Divisible.of(Quantity.of(7, "m"), Quantity.of(3, "m")));
    assertFalse(Divisible.of(Quantity.of(3, "m"), Quantity.of(7, "m")));
  }

  @Test
  public void testQuantityIncompatible() {
    Scalar qs1 = Quantity.of(6, "m");
    Scalar qs2 = Quantity.of(3, "s");
    AssertFail.of(() -> Divisible.of(qs1, qs2));
  }

  @Test
  public void testNumericFail() {
    AssertFail.of(() -> Divisible.of(RealScalar.of(9.), RealScalar.of(3)));
    AssertFail.of(() -> Divisible.of(Quantity.of(9., "m"), Quantity.of(3, "m")));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> Divisible.of(null, RealScalar.of(3)));
    AssertFail.of(() -> Divisible.of(Quantity.of(9, "m"), null));
  }

  @Test
  public void testZeroFail() {
    AssertFail.of(() -> Divisible.of(RealScalar.ONE, RealScalar.ZERO));
    AssertFail.of(() -> Scalars.divides(RealScalar.ZERO, RealScalar.ONE));
  }
}
