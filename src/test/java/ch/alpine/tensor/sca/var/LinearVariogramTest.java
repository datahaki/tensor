// code by jph
package ch.alpine.tensor.sca.var;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.num.Pi;

class LinearVariogramTest {
  @Test
  void testSimple() {
    assertEquals(new LinearVariogram(Pi.VALUE).a(), Pi.VALUE);
  }
}
