// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class SparseEntryTransposeTest {
  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(SparseEntryTranspose.class.getModifiers()));
  }
}
