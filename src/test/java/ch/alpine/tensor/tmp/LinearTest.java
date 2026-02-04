// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LinearTest {
  @Test
  void testThrows() {
    assertThrows(Exception.class, () -> new Linear().evaluate(null, null));
  }
}
