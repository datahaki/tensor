// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

public class ToleranceTest {
  @Test
  public void testTrue() {
    Scalar scalar = Scalars.fromString("1E-20");
    assertEquals(scalar, DoubleScalar.of(1E-20));
    Tolerance.CHOP.requireZero(scalar);
    assertTrue(Tolerance.CHOP.isZero(scalar));
    assertTrue(Tolerance.CHOP.allZero(scalar));
  }

  @Test
  public void testFalse() {
    Scalar scalar = Scalars.fromString("1E-8");
    assertFalse(Tolerance.CHOP.isZero(scalar));
    assertFalse(Tolerance.CHOP.allZero(scalar));
  }
}
