// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class ShannonEntropyTest extends TestCase {
  public void testEvaluation() {
    assertEquals(ShannonEntropy.FUNCTION.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(ShannonEntropy.FUNCTION.apply(RealScalar.of(1E-100)), RealScalar.of(2.302585092994046E-98));
  }
}
