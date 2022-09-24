// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;

class LaguerreLTest {
  @Test
  void testValue() {
    Scalar y = LaguerreL.of(RealScalar.of(8), Pi.VALUE);
    Tolerance.CHOP.requireClose(y, RealScalar.of(-1.220960756502154));
  }

  @Test
  void testHalf() {
    Scalar result = LaguerreL.of(RationalScalar.HALF, RealScalar.of(2.34));
    Tolerance.CHOP.requireClose(result, RealScalar.of(-0.716753249916647));
  }

  @Test
  void testPolynomials() {
    assertEquals(LaguerreL.of(2).coeffs(), Tensors.fromString("{1, -2, 1/2}"));
    assertEquals(LaguerreL.of(3).coeffs(), Tensors.fromString("{1, -3, 3/2, -1/6}"));
    assertEquals(LaguerreL.of(4).coeffs(), Tensors.fromString("{1, -4, 3, -2/3, 1/24}"));
    assertEquals(LaguerreL.of(5).coeffs(), Tensors.fromString("{1, -5, 5, -(5/3), 5/24, -1/120}"));
  }
}
