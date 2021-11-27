// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class ReImTest extends TestCase {
  public void testSimple() {
    assertEquals(ReIm.of(RealScalar.ONE), UnitVector.of(2, 0));
    assertEquals(ReIm.of(ComplexScalar.I), UnitVector.of(2, 1));
  }
}
