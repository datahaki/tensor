// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

public class StringScalarQTest {
  @Test
  public void testOf() {
    assertFalse(StringScalarQ.of(RealScalar.ZERO));
    assertFalse(StringScalarQ.of(Tensors.fromString("{hello}")));
    assertTrue(StringScalarQ.of(StringScalar.of("world")));
  }

  @Test
  public void testAny() {
    assertTrue(StringScalarQ.any(Tensors.of(RealScalar.ONE, StringScalar.of("world"))));
    assertFalse(StringScalarQ.any(Tensors.vector(1, 2, 3, 4)));
    assertFalse(StringScalarQ.any(Tensors.empty()));
    assertTrue(StringScalarQ.any(StringScalar.of("world")));
  }
}
