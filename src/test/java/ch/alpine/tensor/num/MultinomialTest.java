// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class MultinomialTest {
  @Test
  void testEmpty() {
    assertEquals(Multinomial.of(), RealScalar.ONE);
  }

  @Test
  void testSimple() {
    assertEquals(Multinomial.of(1, 2, 1), RealScalar.of(12));
    assertEquals(Multinomial.of(3, 2, 4), RealScalar.of(1260));
    assertEquals(Multinomial.of(3, 0), RealScalar.of(1));
    assertEquals(Multinomial.of(3, 5, 7, 2), RealScalar.of(49008960));
    assertEquals(Multinomial.of(3), RealScalar.of(1));
    assertEquals(Multinomial.of(0, 0, 0, 0), RealScalar.of(1));
    assertEquals(Multinomial.of(0), RealScalar.of(1));
  }

  @Test
  void testLarge() {
    assertEquals(Multinomial.of(1, 2, 100000), RealScalar.of(500030000550003L));
    assertEquals(Multinomial.of(100000, 1, 2), RealScalar.of(500030000550003L));
  }

  @Test
  void testTwoMedium() {
    BigInteger bigInteger = new BigInteger( //
        "376523493564631064367712071965768747782444205128669798396168767743500485766630075466163294008566118208045715304490994009624725072511252178400");
    assertEquals(Multinomial.of(100, 100, 100), RealScalar.of(bigInteger));
  }
}
