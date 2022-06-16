// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;

class CopySignTest {
  @Test
  void testNonZero() {
    assertEquals(Math.copySign(+2.0, +3.0), +2.0);
    assertEquals(Math.copySign(+2.0, -3.0), -2.0);
    assertEquals(Math.copySign(-2.0, +3.0), +2.0);
    assertEquals(Math.copySign(-2.0, -3.0), -2.0);
    assertEquals(CopySign.of(RealScalar.of(+2), RealScalar.of(+3)), RealScalar.of(+2));
    assertEquals(CopySign.of(RealScalar.of(+2), RealScalar.of(-3)), RealScalar.of(-2));
    assertEquals(CopySign.of(RealScalar.of(-2), RealScalar.of(+3)), RealScalar.of(+2));
    assertEquals(CopySign.of(RealScalar.of(-2), RealScalar.of(-3)), RealScalar.of(-2));
  }

  @Test
  void testZero() {
    assertEquals(Math.copySign(+2.0, +0.0), +2.0);
    assertEquals(Math.copySign(+2.0, -0.0), -2.0);
    assertEquals(Math.copySign(-2.0, +0.0), +2.0);
    assertEquals(Math.copySign(-2.0, -0.0), -2.0);
    assertEquals(CopySign.of(RealScalar.of(+2), RealScalar.of(+0.0)), RealScalar.of(+2));
    assertEquals(CopySign.of(RealScalar.of(+2), RealScalar.of(-0.0)), RealScalar.of(+2));
    assertEquals(CopySign.of(RealScalar.of(-2), RealScalar.of(+0.0)), RealScalar.of(+2));
    assertEquals(CopySign.of(RealScalar.of(-2), RealScalar.of(-0.0)), RealScalar.of(+2));
  }

  @Test
  void testQuantity1() {
    Scalar qs1 = Quantity.of(5, "s");
    Scalar qs2 = Quantity.of(-3, "m");
    Scalar qs3 = CopySign.of(qs1, qs2);
    assertEquals(qs3, qs1.negate());
  }

  @Test
  void testQuantity2() {
    Scalar qs1 = Quantity.of(5, "s");
    Scalar qs2 = RealScalar.of(-3);
    Scalar qs3 = CopySign.of(qs1, qs2);
    assertEquals(qs3, qs1.negate());
  }
}
