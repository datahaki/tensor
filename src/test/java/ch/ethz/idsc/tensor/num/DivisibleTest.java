// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DivisibleTest extends TestCase {
  public void testSimple() {
    assertTrue(Divisible.of(RealScalar.of(9), RealScalar.of(3)));
    assertTrue(Divisible.of(RationalScalar.of(18, 7), RationalScalar.of(3, 7)));
    assertFalse(Divisible.of(RationalScalar.of(8, 7), RationalScalar.of(3, 7)));
  }

  public void testNegative() {
    assertTrue(Divisible.of(RealScalar.of(9), RealScalar.of(-3)));
    assertTrue(Divisible.of(RealScalar.of(-3), RealScalar.of(-3)));
  }

  public void testComplex() {
    Scalar c2 = ComplexScalar.of(2, 3);
    Scalar c1 = c2.multiply(RealScalar.of(3));
    assertTrue(Divisible.of(c1, c2));
    assertFalse(Divisible.of(c2, c1));
  }

  public void testGaussian() {
    Scalar c1 = ComplexScalar.of(3, 1);
    Scalar c2 = ComplexScalar.of(2, -1);
    assertTrue(Divisible.of(c1, c2));
    assertFalse(Divisible.of(c2, c1));
  }

  public void testQuantity() {
    assertTrue(Divisible.of(Quantity.of(9, "m"), Quantity.of(3, "m")));
    assertFalse(Divisible.of(Quantity.of(7, "m"), Quantity.of(3, "m")));
    assertFalse(Divisible.of(Quantity.of(3, "m"), Quantity.of(7, "m")));
  }

  public void testQuantityIncompatible() {
    Scalar qs1 = Quantity.of(6, "m");
    Scalar qs2 = Quantity.of(3, "s");
    AssertFail.of(() -> Divisible.of(qs1, qs2));
  }

  public void testNumericFail() {
    AssertFail.of(() -> Divisible.of(RealScalar.of(9.), RealScalar.of(3)));
    AssertFail.of(() -> Divisible.of(Quantity.of(9., "m"), Quantity.of(3, "m")));
  }

  public void testNullFail() {
    AssertFail.of(() -> Divisible.of(null, RealScalar.of(3)));
    AssertFail.of(() -> Divisible.of(Quantity.of(9, "m"), null));
  }

  public void testZeroFail() {
    AssertFail.of(() -> Divisible.of(RealScalar.ONE, RealScalar.ZERO));
    AssertFail.of(() -> Scalars.divides(RealScalar.ZERO, RealScalar.ONE));
  }
}
