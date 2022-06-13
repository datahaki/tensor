// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class StringScalarQTest {
  @Test
  void testOf() {
    assertFalse(RealScalar.ZERO instanceof StringScalar);
    assertFalse(Tensors.fromString("{hello}") instanceof StringScalar);
    assertInstanceOf(StringScalar.class, StringScalar.of("world"));
  }

  @Test
  void testAny() {
    assertTrue(StringScalarQ.any(Tensors.of(RealScalar.ONE, StringScalar.of("world"))));
    assertFalse(StringScalarQ.any(Tensors.vector(1, 2, 3, 4)));
    assertFalse(StringScalarQ.any(Tensors.empty()));
    assertTrue(StringScalarQ.any(StringScalar.of("world")));
  }
}
