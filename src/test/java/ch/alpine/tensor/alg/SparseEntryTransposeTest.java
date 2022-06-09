// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SparseEntryTransposeTest {
  @Test
  public void testVisibility() {
    assertEquals(SparseEntryTranspose.class.getModifiers() & 1, 0);
  }
}
