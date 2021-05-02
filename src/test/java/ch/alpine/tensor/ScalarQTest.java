// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ScalarQTest extends TestCase {
  public void testScalar() {
    assertTrue(ScalarQ.of(Quantity.of(3, "m")));
    assertTrue(ScalarQ.of(GaussScalar.of(3, 11)));
    assertTrue(ScalarQ.of(StringScalar.of("IDSC")));
  }

  public void testVector() {
    assertFalse(ScalarQ.of(Tensors.vector(1, 2, 3)));
  }

  public void testThenThrow() {
    ScalarQ.thenThrow(Tensors.vector(1, 2, 3));
    AssertFail.of(() -> ScalarQ.thenThrow(RealScalar.ONE));
  }
}
