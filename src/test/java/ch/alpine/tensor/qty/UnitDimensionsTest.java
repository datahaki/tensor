// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UnitDimensionsTest {
  @Test
  void test() {
    assertNotNull(UnitDimensions.INSTANCE.get());
  }
}
