// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

class IntTest {
  @Test
  void testSimple() {
    Int int1 = new Int(3);
    assertEquals(int1.intValue(), 3);
    assertEquals(int1.getAndDecrement(), 3); // value is 2 now
    assertEquals(int1.getAndDecrement(), 2); // value is 1 now
    assertEquals(int1.getAndIncrement(), 1); // value is 2 now
    assertEquals(int1.intValue(), 2);
    assertEquals(int1.getAndIncrement(), 2); // value is 3 now
    assertEquals(int1.intValue(), 3);
  }

  @Test
  void testFail() {
    assertFalse(new Int() instanceof Serializable);
  }
}
