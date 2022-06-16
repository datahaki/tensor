// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.chq.FiniteScalarQ;

class Factorial2Test {
  @Test
  void testSimple() {
    Scalar f2_20 = Factorial2.FUNCTION.apply(RealScalar.of(20));
    assertEquals(f2_20, RealScalar.of(3715891200L));
    Tensor tensor = Range.of(0, 10).map(Factorial2.FUNCTION);
    assertEquals(tensor, Tensors.vector(1, 1, 2, 3, 8, 15, 48, 105, 384, 945));
  }

  @Test
  void testNegative1() {
    assertEquals(Factorial2.FUNCTION.apply(RealScalar.ONE.negate()), RealScalar.ONE);
  }

  @Test
  void testLarge() {
    Scalar scalar = Factorial2.FUNCTION.apply(RealScalar.of(100));
    Scalar expect = Scalars.fromString("34243224702511976248246432895208185975118675053719198827915654463488000000000000");
    assertEquals(scalar, expect);
  }

  @Test
  void testNegativeOff() {
    assertEquals(Factorial2.of(-1), RealScalar.ONE);
    assertEquals(Factorial2.of(-3), RealScalar.of(-1));
    assertEquals(Factorial2.of(-5), RationalScalar.of(1, 3));
    assertEquals(Factorial2.of(-7), RationalScalar.of(-1, 15));
  }

  @Test
  void testNegativeEven() {
    assertFalse(FiniteScalarQ.of(Factorial2.of(-2)));
    assertFalse(FiniteScalarQ.of(Factorial2.of(-4)));
  }
}
