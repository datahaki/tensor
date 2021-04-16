// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.re.Pivots;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Power;
import junit.framework.TestCase;

public class IntervalTest extends TestCase {
  public void testInverse1() {
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), Interval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))), //
        Tensors.of(RealScalar.of(2), RealScalar.of(1)));
    Tensor tensor = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    // System.out.println(Pretty.of(tensor.map(Round._3)));
    Pretty.of(tensor);
    // System.out.println();
  }

  public void testInverse2() {
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), Interval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))), //
        Tensors.of(RealScalar.of(2), Interval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))));
    Tensor tensor = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    // System.out.println(Pretty.of(tensor.map(Round._3)));
    Pretty.of(tensor);
    // System.out.println();
  }

  public void testAbs() {
    assertEquals(Abs.FUNCTION.apply(Interval.of(-4, 1)), Interval.of(0, 4));
    assertEquals(Abs.FUNCTION.apply(Interval.of(-4, -2)), Interval.of(2, 4));
  }

  public void testAbsSquared() {
    assertEquals(AbsSquared.FUNCTION.apply(Interval.of(-4, 1)), Interval.of(0, 16));
    assertEquals(AbsSquared.FUNCTION.apply(Interval.of(-4, -2)), Interval.of(4, 16));
  }

  public void testExact() {
    assertTrue(ExactScalarQ.of(Interval.of(-4, 1)));
    assertFalse(ExactScalarQ.of(Interval.of(-4.3, 1.4)));
    assertFalse(ExactScalarQ.of(Interval.of(-4, 1.3)));
    assertFalse(ExactScalarQ.of(Interval.of(-4.1, 1)));
  }

  public void testExp() {
    Exp.FUNCTION.apply(Interval.of(-4, 1));
    Log.FUNCTION.apply(Interval.of(2, 3));
    Interval.of(-4, 1).hashCode();
  }

  public void testPower() {
    Power.of(Interval.of(-4, 1), 7);
  }
}
