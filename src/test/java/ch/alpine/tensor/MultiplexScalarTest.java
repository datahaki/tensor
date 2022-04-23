// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.qty.Quantity;

class MultiplexScalarTest {
  @Test
  void testSimple() {
    assertTrue(Quantity.of(1, "m") instanceof MultiplexScalar);
  }
}
