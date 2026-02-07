// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

class IntTest {
  @Test
  void test() {
    assertFalse(new Int() instanceof Serializable);
  }
}
