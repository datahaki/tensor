// code by jph
package ch.alpine.tensor.opt.fnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;

class FindIntegerTest {
  @Test
  void testMin1() {
    Predicate<Scalar> p = s -> Scalars.lessThan(RealScalar.of(4.5), s);
    Scalar scalar = FindInteger.min(p, Clips.interval(0, 15));
    assertEquals(scalar, RealScalar.of(5));
  }

  @Test
  void testMinFloating() {
    Predicate<Scalar> p = s -> Scalars.lessThan(RealScalar.of(4.5), s);
    Scalar scalar = FindInteger.min(p, BigInteger.ONE);
    assertEquals(scalar, RealScalar.of(5));
  }

  @Test
  void testMin2() {
    Scalar scalar = FindInteger.min(_ -> true, Clips.interval(0, 15));
    assertEquals(scalar, RealScalar.ZERO);
    assertEquals(FindInteger.min(_ -> true, Clips.interval(3, 3)), RealScalar.of(3));
  }

  @Test
  void testMinFail1() {
    assertThrows(Exception.class, () -> FindInteger.min(_ -> false, BigInteger.ONE));
  }

  @Test
  void testMinFail2() {
    assertThrows(Exception.class, () -> FindInteger.min(_ -> false, Clips.interval(0, 15)));
  }

  @Test
  void testMinFail3() {
    Scalar hi = Power.of(2, (256 + 2) * 3);
    Clip clip = Clips.positive(hi);
    assertThrows(Exception.class, () -> FindInteger.min(s -> Scalars.lessEquals(hi, s), clip));
  }
}
