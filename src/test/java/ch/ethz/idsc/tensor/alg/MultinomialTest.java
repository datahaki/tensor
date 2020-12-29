// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class MultinomialTest extends TestCase {
  public void testSimple() {
    assertEquals(Multinomial.of(1, 2, 1), RealScalar.of(12));
    assertEquals(Multinomial.of(3, 2, 4), RealScalar.of(1260));
  }
}
