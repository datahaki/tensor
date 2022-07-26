// code by jph
package ch.alpine.tensor.sca.erf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

class InverseErfTest {
  public static final Chop CHOP_04 = Chop.below(1e-04);

  @Test
  void testSymmetry() {
    Scalar v1 = InverseErf.FUNCTION.apply(RealScalar.of(0.3));
    Tolerance.CHOP.requireClose(v1, RealScalar.of(0.2724627147267544));
    Scalar v2 = InverseErf.FUNCTION.apply(RealScalar.of(-0.3));
    Tolerance.CHOP.requireClose(v2, RealScalar.of(-0.2724627147267544));
  }

  @Test
  void testCorners() {
    assertEquals(InverseErf.of(RealScalar.of(-1)), DoubleScalar.NEGATIVE_INFINITY);
    assertEquals(InverseErf.of(RealScalar.of(+1)), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> InverseErf.FUNCTION.apply(RealScalar.of(+1.3)));
    assertThrows(Throw.class, () -> InverseErf.FUNCTION.apply(RealScalar.of(-1.1)));
  }
}
