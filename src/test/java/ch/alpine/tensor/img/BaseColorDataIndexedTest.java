// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BaseColorDataIndexedTest {
  @Test
  void testVisibility() {
    assertEquals(BaseColorDataIndexed.class.getModifiers() & 1, 0);
  }
}
