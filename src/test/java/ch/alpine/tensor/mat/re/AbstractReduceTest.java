// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AbstractReduceTest {
  @Test
  void testVisibility() {
    assertEquals(AbstractReduce.class.getModifiers() & 1, 0);
  }
}
