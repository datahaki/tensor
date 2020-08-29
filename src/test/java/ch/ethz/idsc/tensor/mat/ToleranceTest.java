// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class ToleranceTest extends TestCase {
  public void testTrue() {
    Scalar scalar = Scalars.fromString("1E-20");
    assertEquals(scalar, DoubleScalar.of(1E-20));
    Tolerance.CHOP.requireZero(scalar);
    assertTrue(Tolerance.CHOP.isZero(scalar));
    assertTrue(Tolerance.CHOP.allZero(scalar));
  }

  public void testFalse() {
    Scalar scalar = Scalars.fromString("1E-8");
    assertFalse(Tolerance.CHOP.isZero(scalar));
    assertFalse(Tolerance.CHOP.allZero(scalar));
  }
}
