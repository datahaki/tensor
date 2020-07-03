// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class DeterminateScalarQTest extends TestCase {
  public void testSimple() {
    assertTrue(DeterminateScalarQ.of(Pi.HALF));
    assertTrue(DeterminateScalarQ.of(RationalScalar.HALF));
  }

  public void testNope() {
    assertFalse(DeterminateScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.INDETERMINATE));
  }

  public void testNullFail() {
    try {
      DeterminateScalarQ.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
