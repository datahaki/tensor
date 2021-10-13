// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    assertTrue(StaticHelper.isFinished(RealScalar.ZERO, RealScalar.ONE));
  }
}
