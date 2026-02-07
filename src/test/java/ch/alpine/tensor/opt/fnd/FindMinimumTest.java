// code by jph
package ch.alpine.tensor.opt.fnd;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;

class FindMinimumTest {
  @Test
  void testFind2() {
    ScalarUnaryOperator suo = s -> Power.of(s.subtract(RealScalar.of(2)), 2);
    Scalar sol = FindMinimum.of(suo).inside(Clips.interval(-3, 500));
    Tolerance.CHOP.requireClose(sol, RealScalar.TWO);
  }
}
