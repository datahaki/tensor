// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RandomFunctionTest {
  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> RandomFunction.of(null));
  }
}
