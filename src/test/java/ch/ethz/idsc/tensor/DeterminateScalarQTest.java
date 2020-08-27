// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.opt.Pi;
import junit.framework.TestCase;

public class DeterminateScalarQTest extends TestCase {
  public void testSimple() {
    assertTrue(DeterminateScalarQ.of(Pi.HALF));
    assertTrue(DeterminateScalarQ.of(RationalScalar.HALF));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("2+3*I")));
    assertTrue(DeterminateScalarQ.of(Scalars.fromString("8.2+3.3*I[m^2]")));
  }

  public void testNope() {
    assertFalse(DeterminateScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(DeterminateScalarQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("abc")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("8.2+NaN*I[m^2]")));
    assertFalse(DeterminateScalarQ.of(Scalars.fromString("NaN+2*I[m*s]")));
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
