// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Clips;

class FindIntegerTest {
  @Test
  void testMin1() {
    Predicate<Scalar> p = s -> Scalars.lessThan(RealScalar.of(4.5), s);
    Scalar scalar = FindInteger.min(p, Clips.interval(0, 15));
    assertEquals(scalar, RealScalar.of(5));
  }

  @Test
  void testMin2() {
    Scalar scalar = FindInteger.min(s -> true, Clips.interval(0, 15));
    assertEquals(scalar, RealScalar.ZERO);
    assertEquals(FindInteger.min(s -> true, Clips.interval(3, 3)), RealScalar.of(3));
  }

  @Test
  void testMinFail() {
    assertThrows(Exception.class, () -> FindInteger.min(s -> false, Clips.interval(0, 15)));
  }
}
