// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class MultinomialTest extends TestCase {
  public void testEmpty() {
    assertEquals(Multinomial.of(), RealScalar.ONE);
  }

  public void testSimple() {
    assertEquals(Multinomial.of(1, 2, 1), RealScalar.of(12));
    assertEquals(Multinomial.of(3, 2, 4), RealScalar.of(1260));
    assertEquals(Multinomial.of(3, 0), RealScalar.of(1));
    assertEquals(Multinomial.of(3, 5, 7, 2), RealScalar.of(49008960));
  }
}
