// code by jph
package ch.alpine.tensor.sca.ply;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class LaguerreLTest {
  @Test
  void test() {
    Scalar result = LaguerreL.of(RationalScalar.HALF, RealScalar.of(2.34));
    Tolerance.CHOP.requireClose(result, RealScalar.of(-0.716753249916647));
  }
}
