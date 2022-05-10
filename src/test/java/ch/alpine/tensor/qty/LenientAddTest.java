// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;

class LenientAddTest {
  @Test
  public void testDifferent() {
    Scalar p = Quantity.of(3, "m");
    Scalar q = Quantity.of(0, "s");
    assertEquals(LenientAdd.of(p, q), p);
    assertEquals(LenientAdd.of(q, p), p);
    assertThrows(TensorRuntimeException.class, () -> p.add(q));
  }

  @Test
  public void testZeros() {
    Scalar p = Quantity.of(0, "m");
    Scalar q = Quantity.of(0, "s");
    assertEquals(LenientAdd.of(p, q), RealScalar.ZERO);
    assertEquals(LenientAdd.of(q, p), RealScalar.ZERO);
    assertThrows(TensorRuntimeException.class, () -> p.add(q));
  }

  @Test
  public void testDifferentFail() {
    Scalar p = Quantity.of(3, "m");
    Scalar q = Quantity.of(1, "s");
    assertThrows(TensorRuntimeException.class, () -> LenientAdd.of(p, q));
    assertThrows(TensorRuntimeException.class, () -> p.add(q));
  }
}
