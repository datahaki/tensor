// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class FromDigitsTest extends TestCase {
  public void testSimple() {
    assertEquals(FromDigits.of(Tensors.vector(1, 2, 3)), RealScalar.of(123));
    assertEquals(FromDigits.of(Tensors.vector(2, 12, 1)), RealScalar.of(321));
  }
}
