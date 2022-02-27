// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class Factorial2Test extends TestCase {
  public void testSimple() {
    Scalar f2_20 = Factorial2.FUNCTION.apply(RealScalar.of(20));
    assertEquals(f2_20, RealScalar.of(3715891200L));
    Tensor tensor = Range.of(0, 10).map(Factorial2.FUNCTION);
    assertEquals(tensor, Tensors.vector(1, 1, 2, 3, 8, 15, 48, 105, 384, 945));
  }

  public void testNegative1() {
    assertEquals(Factorial2.FUNCTION.apply(RealScalar.ONE.negate()), RealScalar.ONE);
  }

  public void testLarge() {
    Scalar scalar = Factorial2.FUNCTION.apply(RealScalar.of(100));
    Scalar expect = Scalars.fromString("34243224702511976248246432895208185975118675053719198827915654463488000000000000");
    assertEquals(scalar, expect);
  }

  public void testFail() {
    AssertFail.of(() -> Factorial2.of(-2));
  }
}
