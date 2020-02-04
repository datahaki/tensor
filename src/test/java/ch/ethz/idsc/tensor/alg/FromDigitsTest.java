// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class FromDigitsTest extends TestCase {
  public void testSimple() {
    assertEquals(FromDigits.of(Tensors.vector(1, 2, 3)), RealScalar.of(123));
    assertEquals(FromDigits.of(Tensors.vector(2, 12, 1)), RealScalar.of(321));
  }
}
