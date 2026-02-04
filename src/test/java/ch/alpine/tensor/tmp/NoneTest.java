// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NoneTest {
  @Test
  void test() {
    assertEquals(new None().toString(), "None");
  }
}
