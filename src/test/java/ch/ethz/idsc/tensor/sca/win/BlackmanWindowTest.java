// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BlackmanWindowTest extends TestCase {
  public void testSimple() {
    Scalar result = BlackmanWindow.FUNCTION.apply(RealScalar.of(0.2));
    Scalar expect = RealScalar.of(0.50978713763747791812); // checked with Mathematica
    Chop._12.requireClose(result, expect);
  }

  public void testFail() {
    assertEquals(BlackmanWindow.FUNCTION.apply(RealScalar.of(-0.51)), RealScalar.ZERO);
  }
}
