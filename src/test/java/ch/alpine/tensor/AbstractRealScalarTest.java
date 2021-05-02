// code by jph
package ch.alpine.tensor;

import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class AbstractRealScalarTest extends TestCase {
  public void testArcTan() {
    AssertFail.of(() -> ArcTan.of(RealScalar.of(2.3), GaussScalar.of(3, 7)));
    AssertFail.of(() -> ArcTan.of(GaussScalar.of(3, 7), RealScalar.of(2.3)));
  }

  public void testRange() {
    assertEquals(Math.log(AbstractRealScalar.LOG_HI), Math.log1p(AbstractRealScalar.LOG_HI - 1));
    assertEquals(Math.log(AbstractRealScalar.LOG_LO), Math.log1p(AbstractRealScalar.LOG_LO - 1));
  }

  public void testPower00() {
    Scalar one = Power.of(0, 0);
    ExactScalarQ.require(one);
    assertEquals(one, RealScalar.ONE);
  }

  public void testPower00Numeric() {
    Scalar one = Power.of(0.0, 0.0);
    ExactScalarQ.require(one);
    assertEquals(one, RealScalar.ONE);
  }

  public void testPowerFail() {
    AssertFail.of(() -> Power.of(1, GaussScalar.of(2, 7)));
  }
}
