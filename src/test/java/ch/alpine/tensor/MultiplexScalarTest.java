// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.qty.Quantity;

class MultiplexScalarTest {
  @Test
  void testSimple() {
    assertInstanceOf(MultiplexScalar.class, Quantity.of(1, "m"));
  }
}
