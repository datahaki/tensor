// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

public class ShannonEntropyTest {
  @Test
  public void testEvaluation() {
    assertEquals(ShannonEntropy.FUNCTION.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(ShannonEntropy.FUNCTION.apply(RealScalar.of(1E-100)), RealScalar.of(2.302585092994046E-98));
  }
}
