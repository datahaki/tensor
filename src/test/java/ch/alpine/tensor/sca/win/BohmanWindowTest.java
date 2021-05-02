// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class BohmanWindowTest extends TestCase {
  public void testSimple() {
    Tolerance.CHOP.requireClose(BohmanWindow.FUNCTION.apply(RealScalar.of(0.3)), RealScalar.of(0.1791238937062839));
  }
}
