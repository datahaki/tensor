// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class AbstractReduceTest {
  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(AbstractReduce.class.getModifiers()));
  }
}
