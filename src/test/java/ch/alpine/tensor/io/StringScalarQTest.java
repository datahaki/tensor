// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class StringScalarQTest extends TestCase {
  public void testOf() {
    assertFalse(StringScalarQ.of(RealScalar.ZERO));
    assertFalse(StringScalarQ.of(Tensors.fromString("{hello}")));
    assertTrue(StringScalarQ.of(StringScalar.of("world")));
  }

  public void testAny() {
    assertTrue(StringScalarQ.any(Tensors.of(RealScalar.ONE, StringScalar.of("world"))));
    assertFalse(StringScalarQ.any(Tensors.vector(1, 2, 3, 4)));
    assertFalse(StringScalarQ.any(Tensors.empty()));
    assertTrue(StringScalarQ.any(StringScalar.of("world")));
  }
}
