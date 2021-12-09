// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LenientAddTest extends TestCase {
  public void testDifferent() {
    Scalar p = Quantity.of(3, "m");
    Scalar q = Quantity.of(0, "s");
    assertEquals(LenientAdd.of(p, q), p);
    assertEquals(LenientAdd.of(q, p), p);
    AssertFail.of(() -> p.add(q));
  }

  public void testZeros() {
    Scalar p = Quantity.of(0, "m");
    Scalar q = Quantity.of(0, "s");
    assertEquals(LenientAdd.of(p, q), RealScalar.ZERO);
    assertEquals(LenientAdd.of(q, p), RealScalar.ZERO);
    AssertFail.of(() -> p.add(q));
  }

  public void testDifferentFail() {
    Scalar p = Quantity.of(3, "m");
    Scalar q = Quantity.of(1, "s");
    AssertFail.of(() -> LenientAdd.of(p, q));
    AssertFail.of(() -> p.add(q));
  }
}
