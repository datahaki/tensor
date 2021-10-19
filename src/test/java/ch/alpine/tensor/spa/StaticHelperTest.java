// code by jph
package ch.alpine.tensor.spa;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testFallbackFail() {
    AssertFail.of(() -> StaticHelper.checkFallback(RealScalar.ONE));
    AssertFail.of(() -> StaticHelper.checkFallback(Quantity.of(0, "m")));
  }

  public void testVisibility() {
    assertEquals(StaticHelper.class.getModifiers() & 1, 0);
  }
}
