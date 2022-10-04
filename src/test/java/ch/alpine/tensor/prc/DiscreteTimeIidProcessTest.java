// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.pdf.c.LogNormalDistribution;

class DiscreteTimeIidProcessTest {
  @Test
  void testToString() {
    RandomProcess randomProcess = DiscreteTimeIidProcess.of(LogNormalDistribution.standard());
    assertTrue(randomProcess.toString().startsWith("DiscreteTimeIidProcess["));
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> DiscreteTimeIidProcess.of(null));
  }
}
