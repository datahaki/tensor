// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

class FindRootTest {
  @Test
  public void testJackpot() {
    FindRoot findRoot = FindRoot.of(x -> x, Chop._12);
    Chop._10.requireClose(findRoot.between(RealScalar.of(-1), RealScalar.of(1)), RealScalar.ZERO);
    Chop._10.requireClose(findRoot.between(RealScalar.of(-1.0), RealScalar.of(1.0)), RealScalar.ZERO);
  }

  @Test
  public void testSimple() {
    FindRoot findRoot = FindRoot.of(Cos.FUNCTION, Chop._12);
    Chop._10.requireClose(findRoot.between(RealScalar.of(0.0), RealScalar.of(4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.between(RealScalar.of(1.0), RealScalar.of(4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.between(RealScalar.of(1.0), RealScalar.of(2.0)), Pi.HALF);
    assertThrows(TensorRuntimeException.class, () -> findRoot.between(RealScalar.of(0.0), RealScalar.of(1.0)));
  }

  @Test
  public void testSimple2() {
    FindRoot findRoot = FindRoot.of(z -> Cos.FUNCTION.apply(z).negate(), Chop._12);
    Chop._10.requireClose(findRoot.between(RealScalar.of(0.0), RealScalar.of(4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.between(RealScalar.of(1.0), RealScalar.of(4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.between(RealScalar.of(1.0), RealScalar.of(2.0)), Pi.HALF);
    assertThrows(TensorRuntimeException.class, () -> findRoot.between(RealScalar.of(0.0), RealScalar.of(1.0)));
  }

  @Test
  public void testInitZero() {
    FindRoot findRoot = FindRoot.of(z -> Sin.FUNCTION.apply(z).negate(), Chop._12);
    Chop._10.requireClose(findRoot.between(RealScalar.of(0.0), RealScalar.of(4.0)), RealScalar.ZERO);
    Chop._10.requireClose(findRoot.between(RealScalar.of(0.5), Pi.VALUE), Pi.VALUE);
    Chop._10.requireClose(findRoot.between(RealScalar.of(1.0), RealScalar.of(4.0)), Pi.VALUE);
    assertThrows(TensorRuntimeException.class, () -> findRoot.between(RealScalar.of(1.0), RealScalar.of(2.0)));
  }

  public static Scalar func(Scalar c) {
    Scalar T = RealScalar.of(300);
    Scalar r = RealScalar.of(700);
    Scalar t2 = Log.FUNCTION.apply(c.divide(RealScalar.ONE.subtract(c))).multiply(T);
    Scalar t1 = Times.of(c.subtract(RationalScalar.HALF), RealScalar.of(4), r);
    return t1.subtract(t2);
  }

  @Test
  public void testTs() {
    FindRoot findRoot = FindRoot.of(FindRootTest::func);
    Scalar x = findRoot.between(RealScalar.of(0.001), RealScalar.of(0.499));
    Tolerance.CHOP.requireClose(x, RealScalar.of(0.01024075603728308));
    // System.out.println("1/2 -> " + func(RationalScalar.HALF));
    // System.out.println(x + " -> " + func(x));
    // System.out.println(x + " -> " + func(RealScalar.ONE.subtract(x)));
  }

  @Test
  public void testLinear2() {
    Scalar scalar = FindRoot.linear(RealScalar.of(10), RealScalar.of(11), RealScalar.of(5), RealScalar.of(-2));
    assertEquals(scalar, RationalScalar.of(75, 7));
  }

  @Test
  public void testOther() {
    Scalar scalar = FindRoot.linear(RealScalar.of(5), RealScalar.of(6), RealScalar.of(2), RealScalar.of(-1));
    assertEquals(scalar, RationalScalar.of(5 * 3 + 2, 3));
  }
}
