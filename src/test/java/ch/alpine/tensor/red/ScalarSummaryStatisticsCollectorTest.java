// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ScalarSummaryStatisticsCollectorTest {
  @Test
  public void testFinisher() {
    assertNull(ScalarSummaryStatisticsCollector.INSTANCE.finisher().apply(null));
  }
}
