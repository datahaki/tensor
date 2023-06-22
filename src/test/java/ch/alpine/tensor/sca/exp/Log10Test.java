// code by jph
package ch.alpine.tensor.sca.exp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;

class Log10Test {
  @Test
  void testOne() {
    Scalar scalar = Log10.FUNCTION.apply(RealScalar.ONE);
    assertTrue(Scalars.isZero(scalar));
  }

  @Test
  void testLog() {
    Scalar s = DoubleScalar.of(-3);
    Scalar r = ComplexScalar.of(0.4771212547196624, 1.3643763538418412);
    Tolerance.CHOP.requireClose(Log10.FUNCTION.apply(s), r);
    assertEquals(Log10.FUNCTION.apply(RealScalar.ZERO), DoubleScalar.NEGATIVE_INFINITY);
  }

  @Test
  void testComplex() {
    Scalar s = ComplexScalar.of(-2, 1);
    Scalar r = ComplexScalar.of(0.3494850021680094, 1.1630167557051545);
    Tolerance.CHOP.requireClose(Log10.FUNCTION.apply(s), r);
  }

  @Test
  void testBase() {
    Scalar scalar = DoubleScalar.of(1412.123);
    Tolerance.CHOP.requireClose(Log10.FUNCTION.apply(scalar), Log.base(RealScalar.of(10)).apply(scalar));
  }

  @Test
  void testFail() {
    Scalar scalar = GaussScalar.of(6, 7);
    assertThrows(Throw.class, () -> Log10.FUNCTION.apply(scalar));
  }
}
