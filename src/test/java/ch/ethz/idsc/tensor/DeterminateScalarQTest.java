// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class DeterminateScalarQTest extends TestCase {
  public void testSimple() {
    assertTrue(DeterminateScalarQ.of(Pi.HALF));
  }
}
