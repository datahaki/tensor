// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

public class MultinomialTest {
  @Test
  public void testEmpty() {
    assertEquals(Multinomial.of(), RealScalar.ONE);
  }

  @Test
  public void testSimple() {
    assertEquals(Multinomial.of(1, 2, 1), RealScalar.of(12));
    assertEquals(Multinomial.of(3, 2, 4), RealScalar.of(1260));
    assertEquals(Multinomial.of(3, 0), RealScalar.of(1));
    assertEquals(Multinomial.of(3, 5, 7, 2), RealScalar.of(49008960));
  }
}
