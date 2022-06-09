// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class RiffleTest {
  @Test
  public void testSimple() {
    Tensor vector = Riffle.of(Tensors.vector(1, 2, 3, 4, 5), RealScalar.ZERO);
    assertEquals(vector, Tensors.vector(1, 0, 2, 0, 3, 0, 4, 0, 5));
  }

  @Test
  public void testEmpty() {
    Tensor vector = Riffle.of(Tensors.empty(), RealScalar.ZERO);
    assertTrue(Tensors.isEmpty(vector));
  }

  @Test
  public void testScalarFail() {
    assertThrows(IllegalArgumentException.class, () -> Riffle.of(RealScalar.ZERO, RealScalar.ZERO));
  }
}
