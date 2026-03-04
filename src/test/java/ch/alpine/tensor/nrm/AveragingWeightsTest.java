// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Total;

class AveragingWeightsTest {
  @RepeatedTest(7)
  void testSimple(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor weights = AveragingWeights.of(n);
    assertEquals(weights.length(), n);
    assertEquals(Total.ofVector(weights), RealScalar.ONE);
    assertEquals(weights.get(0), Rational.of(1, n));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> AveragingWeights.of(0));
    assertThrows(Exception.class, () -> AveragingWeights.of(-2));
  }
}
