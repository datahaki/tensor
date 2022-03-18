// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class HammingWindowTest {
  @Test
  public void testSimple() {
    Scalar result = HammingWindow.FUNCTION.apply(RealScalar.of(0.2));
    Scalar expect = RealScalar.of(0.68455123656247599796); // checked with Mathematica
    Tolerance.CHOP.requireClose(result, expect);
  }

  @Test
  public void testOutside() {
    Scalar scalar = HammingWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }

  @Test
  public void testQuantityFail() {
    AssertFail.of(() -> HammingWindow.FUNCTION.apply(Quantity.of(0, "s")));
    AssertFail.of(() -> HammingWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
