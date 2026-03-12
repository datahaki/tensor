// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;

class SiUnitDimensionsTest {
  @Test
  void test() {
    Scalar a = Quantity.of(300, "m");
    Scalar b = Quantity.of(1, "km");
    Scalar c = a.add(b);
    assertEquals(c, Quantity.of(1300, "m"));
  }
}
