// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class MinMaxCollectorTest {
  @Test
  void test() {
    assertFalse(Modifier.isPublic(MinMaxCollector.class.getModifiers()));
  }
}
