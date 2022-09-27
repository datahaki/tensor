// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.qty.Quantity;

class CenteredIntervalTest {
  @Test
  void test() {
    Scalar x = CenteredInterval.of(10, 2);
    Scalar y = CenteredInterval.of(-8, 7);
    assertEquals(x.add(y), CenteredInterval.of(2, 9));
    assertEquals(x.negate(), CenteredInterval.of(-10, 2));
    ExactScalarQ.require(x);
  }

  @Test
  void testDateObject() {
    Scalar do1 = CenteredInterval.of(DateTime.now(), Quantity.of(3, "s"));
    Scalar do2 = CenteredInterval.of(Quantity.of(9, "s"), Quantity.of(1, "s"));
    assertEquals(do1.zero(), Quantity.of(0, "s"));
    assertEquals(do1.one(), RealScalar.ONE);
    do1.add(do2);
    do1.add(Quantity.of(4, "s"));
  }
}
