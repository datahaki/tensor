// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

public class FromDigitsTest {
  @Test
  public void testSimple() {
    assertEquals(FromDigits.of(Tensors.vector(1, 2, 3)), RealScalar.of(123));
    assertEquals(FromDigits.of(Tensors.vector(2, 12, 1)), RealScalar.of(321));
  }
}
