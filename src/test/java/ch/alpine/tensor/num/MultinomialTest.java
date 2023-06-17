// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
  }

  @Test
  void testLarge() {
    assertEquals(Multinomial.of(1, 2, 100000), RealScalar.of(500030000550003L));
    assertEquals(Multinomial.of(100000, 1, 2), RealScalar.of(500030000550003L));
  }
}
