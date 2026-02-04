package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class HoldTest {
  @Test
  void testDouble() {
    Scalar a = Hold.zero();
    Scalar b = DoubleScalar.of(0);
    Chop.NONE.requireClose(a, b);
    Chop.NONE.requireClose(b, a);
    assertEquals(a.number(), 0);
  }

  @Disabled
  @Test
  void testGaussian() {
    Scalar a = Hold.zero();
    Scalar b = GaussScalar.of(3, 5);
    assertEquals(a.add(b), b);
    assertEquals(b.add(a), b);
  }

  @Test
  void testSet() {
    Set<Object> set = new HashSet<>();
    set.add(Hold.zero());
    set.add(Hold.zero());
    assertEquals(set.size(), 1);
  }

  @Test
  void testImplicit() {
    Scalar a = Hold.zero();
    assertTrue(Scalars.isZero(a));
  }

  @Test
  void testDateTime() {
    Scalar a = Hold.zero().multiply(DateTime.now());
    assertEquals(a, Quantity.of(0, "s"));
  }
}
